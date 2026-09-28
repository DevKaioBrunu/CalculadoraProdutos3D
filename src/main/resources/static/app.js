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
    return Number(value);
}

document.querySelector("#calculator-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const payload = Object.fromEntries([...form.entries()].map(([key, value]) => [key, numberValue(value)]));

    try {
        const response = await fetch("/api/calculadora/precificar", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (!response.ok) throw new Error(await response.text());
        const result = await response.json();
        document.querySelector("#result-filament").textContent = currency.format(result.filamentCost);
        document.querySelector("#result-energy").textContent = currency.format(result.energyCost);
        document.querySelector("#result-total").textContent = currency.format(result.totalCost);
        document.querySelector("#result-sale").textContent = currency.format(result.salePrice);
        document.querySelector("#calculation-result").classList.remove("hidden");
        showFeedback("Cálculo realizado com sucesso.");
    } catch {
        showFeedback("Não foi possível calcular. Verifique os valores e se a API está disponível.", true);
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
    document.querySelector("#filament-price").value = filament.pricePerKg;
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
    const response = await fetch(`/api/filamentos/${id}`, { method: "DELETE" });
    if (!response.ok) {
        showFeedback(await responseError(response), true);
        return;
    }
    showFeedback("Filamento excluído com sucesso.");
    await loadFilaments();
}

filamentForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const id = document.querySelector("#filament-id").value;
    const response = await fetch(id ? `/api/filamentos/${id}` : "/api/filamentos", {
        method: id ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(filamentPayload())
    });
    if (!response.ok) {
        showFeedback(await responseError(response), true);
        return;
    }
    resetFilamentForm();
    showFeedback(id ? "Filamento atualizado com sucesso." : "Filamento cadastrado com sucesso.");
    await loadFilaments();
});

document.querySelector("#filament-cancel").addEventListener("click", resetFilamentForm);
document.querySelector("#refresh-filaments").addEventListener("click", loadFilaments);
loadFilaments();

function escapeHtml(value) {
    const element = document.createElement("span");
    element.textContent = value;
    return element.innerHTML;
}
