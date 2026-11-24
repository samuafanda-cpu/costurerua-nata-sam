const search = document.querySelector('#buscar');
const rows = [...document.querySelectorAll('.client-row')];
function filterClients() {
  const query = search.value.trim().toLocaleLowerCase('es');
  let count = 0;
  for (const row of rows) {
    const values = [...row.querySelectorAll('input:not([type=hidden])')].map(input => input.value).join(' ').toLocaleLowerCase('es');
    row.hidden = !values.includes(query);
    if (!row.hidden) count++;
  }
  document.querySelector('#resultado').textContent = `${count} cliente(s) visible(s)`;
}
if (search) { search.addEventListener('input', filterClients); filterClients(); }
document.querySelectorAll('button[value=eliminar]').forEach(button => button.addEventListener('click', event => { if (!confirm('¿Deseas eliminar este cliente?')) event.preventDefault(); }));
