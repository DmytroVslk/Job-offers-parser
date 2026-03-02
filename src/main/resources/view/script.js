// Функція пошуку вакансій
async function searchJobs() {
    const location = document.getElementById('location').value;
    const position = document.getElementById('position').value;
    const statusDiv = document.getElementById('status');
    const searchBtn = document.getElementById('searchBtn');
    const tableBody = document.getElementById('jobsTableBody');
    const jobCount = document.getElementById('jobCount');
    
    // Показати статус завантаження
    statusDiv.textContent = `🔄 Searching for jobs in ${location}...`;
    statusDiv.className = 'status loading';
    searchBtn.disabled = true;
    
    // Очистити таблицю
    tableBody.innerHTML = '<tr><td colspan="4" style="text-align: center;">Loading...</td></tr>';
    
    try {
        // Викликаємо API сервера
        const response = await fetch(`/search?location=${encodeURIComponent(location)}&position=${encodeURIComponent(position)}`);
        
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        
        const jobs = await response.json();
        
        // Очищаємо таблицю
        tableBody.innerHTML = '';
        
        if (jobs.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="4" class="empty-state">
                        <p>😔 No jobs found. Try different filters.</p>
                    </td>
                </tr>
            `;
            statusDiv.textContent = 'No jobs found. Try adjusting your search.';
            statusDiv.className = 'status';
        } else {
            // Додаємо кожну вакансію в таблицю
            jobs.forEach(job => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td><strong>${escapeHtml(job.title)}</strong></td>
                    <td>${escapeHtml(job.company)}</td>
                    <td>${escapeHtml(job.location)}</td>
                    <td>
                        <a href="${escapeHtml(job.url)}" target="_blank" class="apply-btn">
                            View Job →
                        </a>
                    </td>
                `;
                tableBody.appendChild(row);
            });
            
            statusDiv.textContent = `✅ Found ${jobs.length} jobs in ${location}`;
            statusDiv.className = 'status success';
        }
        
        jobCount.textContent = jobs.length;
        
    } catch (error) {
        console.error('Error:', error);
        statusDiv.textContent = '❌ Error loading jobs. Please try again.';
        statusDiv.className = 'status';
        tableBody.innerHTML = `
            <tr>
                <td colspan="4" style="text-align: center; color: #dc3545;">
                    Error: ${error.message}
                </td>
            </tr>
        `;
    } finally {
        searchBtn.disabled = false;
    }
}

// Захист від XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Автоматичний пошук при завантаженні сторінки (опціонально)
window.addEventListener('load', () => {
    console.log('🚀 Job Aggregator loaded');
});
