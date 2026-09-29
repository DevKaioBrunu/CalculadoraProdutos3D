const feedback = document.querySelector("#feedback");
const currency = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });

function showFeedback(message, error = false) {
    feedback.textContent = message;
    feedback.classList.toggle("error", error);
}

async function responseError(response) {
    let detail = "";
    try {
        const body = await response.json();
        detail = body.message || body.error || "";
    } catch {
        detail = "";
    }
    return detail ? `${response.status}: ${detail}` : `Não foi possível concluir a operação (${response.status}).`;
}

function numberValue(value) {
    const raw = String(value).trim();
    const normalized = raw.includes(",")
        ? raw.replace(/\./g, "").replace(",", ".")
        : raw;
    return normalized === "" ? null : Number(normalized);
}

function brazilianDecimal(value) {
    return Number(value).toLocaleString("pt-BR", {
        useGrouping: false,
        maximumFractionDigits: 10
    });
}

function setButtonBusy(button, busy, busyLabel) {
    button.disabled = busy;
    if (busy) {
        button.dataset.originalLabel = button.textContent;
        button.textContent = busyLabel;
    } else if (button.dataset.originalLabel) {
        button.textContent = button.dataset.originalLabel;
        delete button.dataset.originalLabel;
    }
}

document.querySelectorAll("[data-tab]").forEach((tab) => {
    tab.addEventListener("click", () => {
        document.querySelectorAll("[data-tab]").forEach((item) => item.classList.toggle("active", item === tab));
        document.querySelectorAll("[data-view]").forEach((view) => {
            const active = view.id === tab.dataset.tab;
            view.hidden = !active;
            view.classList.toggle("active-view", active);
        });
        if (tab.dataset.tab === "filaments") loadFilaments();
    });
});

document.querySelector("#calculator-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const submit = document.querySelector("#calculator-submit");
    const form = new FormData(event.currentTarget);
    const payload = Object.fromEntries([...form.entries()].map(([key, value]) => [key, numberValue(value)]));

    try {
        setButtonBusy(submit, true, "Calculando...");
        const response = await fetch("/api/calculadora/precificar", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (!response.ok) throw new Error(await responseError(response));
        const result = await response.json();
        document.querySelector("#result-filament").textContent = currency.format(result.filamentCost);
        document.querySelector("#result-energy").textContent = currency.format(result.energyCost);
        document.querySelector("#result-total").textContent = currency.format(result.totalCost);
        document.querySelector("#result-sale").textContent = currency.format(result.salePrice);
        showFeedback("Cálculo realizado com sucesso.");
    } catch (error) {
        showFeedback(error.message || "Não foi possível calcular. Verifique os valores e se a API está disponível.", true);
    } finally {
        setButtonBusy(submit, false);
    }
});

const filamentForm = document.querySelector("#filament-form");

function filamentPayload() {
    return {
        name: document.querySelector("#filament-name").value.trim(),
        brand: document.querySelector("#filament-brand").value.trim(),
        material: document.querySelector("#filament-material").value.trim(),
        color: document.querySelector("#filament-color").value.trim() || null,
        pricePerKg: numberValue(document.querySelector("#filament-price").value)
    };
}

function resetFilamentForm() {
    filamentForm.reset();
    document.querySelector("#filament-id").value = "";
    document.querySelector("#filament-submit").textContent = "Cadastrar filamento";
    document.querySelector("#filament-cancel").classList.add("hidden");
}

function editFilament(filament) {
    document.querySelector("#filament-id").value = filament.id;
    document.querySelector("#filament-name").value = filament.name;
    document.querySelector("#filament-brand").value = filament.brand;
    document.querySelector("#filament-material").value = filament.material;
    document.querySelector("#filament-color").value = filament.color || "";
    document.querySelector("#filament-price").value = brazilianDecimal(filament.pricePerKg);
    document.querySelector("#filament-submit").textContent = "Salvar alterações";
    document.querySelector("#filament-cancel").classList.remove("hidden");
    filamentForm.scrollIntoView({ behavior: "smooth", block: "center" });
}

async function loadFilaments() {
    const body = document.querySelector("#filaments-body");
    try {
        const response = await fetch("/api/filamentos");
        if (!response.ok) throw new Error(await responseError(response));
        const filaments = await response.json();
        body.replaceChildren();
        if (filaments.length === 0) {
            body.innerHTML = '<tr><td colspan="6" class="empty">Nenhum filamento cadastrado.</td></tr>';
            return;
        }
        filaments.forEach((filament) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${escapeHtml(filament.name)}</td>
                <td>${escapeHtml(filament.brand)}</td>
                <td>${escapeHtml(filament.material)}</td>
                <td>${escapeHtml(filament.color || "-")}</td>
                <td>${currency.format(filament.pricePerKg)}</td>
                <td class="table-actions">
                    <button class="button secondary edit" type="button">Editar</button>
                    <button class="button delete" type="button">Excluir</button>
                </td>`;
            row.querySelector(".edit").addEventListener("click", () => editFilament(filament));
            row.querySelector(".delete").addEventListener("click", () => deleteFilament(filament.id));
            body.appendChild(row);
        });
    } catch (error) {
        body.innerHTML = '<tr><td colspan="6" class="empty">Não foi possível carregar os filamentos.</td></tr>';
        showFeedback(error.message, true);
    }
}

async function deleteFilament(id) {
    if (!window.confirm("Deseja realmente excluir este filamento?")) return;
    try {
        const response = await fetch(`/api/filamentos/${id}`, { method: "DELETE" });
        if (!response.ok) throw new Error(await responseError(response));
        showFeedback("Filamento excluído com sucesso.");
        await loadFilaments();
    } catch (error) {
        showFeedback(error.message, true);
    }
}

filamentForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const id = document.querySelector("#filament-id").value;
    const submit = document.querySelector("#filament-submit");
    try {
        setButtonBusy(submit, true, id ? "Salvando..." : "Cadastrando...");
        const response = await fetch(id ? `/api/filamentos/${id}` : "/api/filamentos", {
            method: id ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(filamentPayload())
        });
        if (!response.ok) throw new Error(await responseError(response));
        resetFilamentForm();
        showFeedback(id ? "Filamento atualizado com sucesso." : "Filamento cadastrado com sucesso.");
        await loadFilaments();
    } catch (error) {
        showFeedback(error.message, true);
    } finally {
        setButtonBusy(submit, false);
    }
});

document.querySelector("#filament-cancel").addEventListener("click", resetFilamentForm);
document.querySelector("#refresh-filaments").addEventListener("click", loadFilaments);
loadFilaments();

function escapeHtml(value) {
    const element = document.createElement("span");
    element.textContent = value;
    return element.innerHTML;
}
