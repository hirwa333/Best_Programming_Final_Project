// Sidebar HTML injected into every page
function renderSidebar() {
  const html = `
  <div class="sidebar">
    <div class="sidebar-logo">
      <div class="logo-icon">🚛</div>
      <h2>Impano Gateway Logistics</h2>
      <p>SLTMS v1.0</p>
    </div>
    <div class="sidebar-user">
      <div class="user-name" id="sidebar-user-name">Admin</div>
      <div class="user-role" id="sidebar-user-role">ADMIN</div>
    </div>
    <nav>
      <div class="nav-section">Main</div>
      <a href="dashboard.html"  class="nav-link"><span class="icon">📊</span> Dashboard</a>
      <a href="shipments.html"  class="nav-link"><span class="icon">📦</span> Shipments</a>
      <a href="clients.html"    class="nav-link"><span class="icon">👤</span> Clients</a>
      <a href="drivers.html"    class="nav-link"><span class="icon">🚗</span> Drivers</a>
      <a href="vehicles.html"   class="nav-link"><span class="icon">🚛</span> Vehicles</a>
      <div class="nav-section">Finance</div>
      <a href="invoices.html"   class="nav-link"><span class="icon">🧾</span> Invoices</a>
      <a href="reports.html"    class="nav-link"><span class="icon">📈</span> Reports</a>
    </nav>
    <div class="sidebar-footer">
      <button class="btn-logout" onclick="logout()">🚪 Logout</button>
    </div>
  </div>`;
  document.body.insertAdjacentHTML('afterbegin', html);
  setActiveNav();
  renderSidebarUser();
}
