let cpuData = [];
let memData = [];
const maxPoints = 20;
let coreCharts = [];
let memorySeries = [];



const chart = new ApexCharts(document.querySelector("#chart"), {
    chart: { type: "line", height: 300 },
    series: [
        { name: "CPU (%)", data: [] },
        { name: "Memory (%)", data: [] }
    ],
    xaxis: { type: "datetime" },
	yaxis: { min: 0, max: 100 }
});
chart.render();

function initCoreCharts(coreCount) {
    const container = document.getElementById("core-charts");
    container.innerHTML = "";
    coreCharts = [];

    for (let i = 0; i < coreCount; i++) {
        const div = document.createElement("div");
        div.id = `core-chart-${i}`;
        div.style.marginBottom = "16px";
        container.appendChild(div);

        const chart = new ApexCharts(div, {
            chart: { type: "line", height: 200 },
            series: [{ name: `Core ${i}`, data: [] }],
            xaxis: { type: "datetime" },
			yaxis: { min: 0, max: 100 },
            title: { text: `Core ${i}`, align: "left" }
        });
        chart.render();
        coreCharts.push({ chart, data: [] });
    }
}


function registerApp(event) {
    event.preventDefault();
    const form = document.getElementById("add-form");
    const formData = new FormData(form);
    const data = {};
    formData.forEach((value, key) => data[key] = value);

    fetch("/api/apps/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
    .then(res => {
        if (!res.ok) throw new Error("登録失敗");
        alert("アプリを登録しました！");
        form.reset();
        loadAppCards();
    })
    .catch(err => {
        alert("エラー: " + err.message);
    });
}

function fetchResource() {
    fetch("/api/resource")
        .then(res => res.json())
        .then(data => {
            const now = new Date().getTime();

            // メモリ使用率（全体グラフ）
            memorySeries.push({ x: now, y: data.memory });
            if (memorySeries.length > maxPoints) memorySeries.shift();
            chart.updateSeries([{ name: "Memory (%)", data: memorySeries }]);

            // コア別CPU使用率
            const cpuArray = data.cpu;
            if (coreCharts.length !== cpuArray.length) {
                initCoreCharts(cpuArray.length);
            }

            cpuArray.forEach((load, i) => {
                const entry = coreCharts[i];
                entry.data.push({ x: now, y: load });
                if (entry.data.length > maxPoints) entry.data.shift();
                entry.chart.updateSeries([{ name: `Core ${i}`, data: entry.data }]);
            });
        });
}


function fetchAppList() {
    fetch("/api/apps/status")
        .then(res => res.json())
        .then(apps => {
            const container = document.getElementById("app-list");
            container.innerHTML = "";
            for (const appId in apps) {
                const status = apps[appId];
                const div = document.createElement("div");
                div.className = "app-card";
                div.innerHTML = `
                    <strong>${appId}</strong> - 状態: ${status ? "起動中" : "停止中"}<br/>
                    <button onclick="operate('${appId}', 'start')">起動</button>
                    <button onclick="operate('${appId}', 'stop')">停止</button>
                    <button onclick="operate('${appId}', 'restart')">再起動</button>
                `;
                container.appendChild(div);
            }
        });
}

function operate(appId, action) {
    fetch(`/api/apps/${action}?appId=${appId}`, { method: "POST" })
        .then(() => {
            setTimeout(fetchAppList, 1000);
        });
}
function loadAppCards() {
    Promise.all([
        fetch("/api/apps/list").then(res => res.json()),
        fetch("/api/apps/status").then(res => res.json())
    ]).then(([apps, statusMap]) => {
        const container = document.getElementById("app-list");
        container.innerHTML = "";
        for (const app of apps) {
            const running = statusMap[app.id];
            const div = document.createElement("div");
            div.className = "app-card";
            div.innerHTML = `
                <strong>${app.name}</strong>（ID: ${app.id}）<br/>
                <span style="color:${running ? 'green' : 'red'}">状態: ${running ? '起動中' : '停止中'}</span><br/>
                <b>PID:</b> ${app.pid ? app.pid : "未起動"}<br/>
                <b>Javaパス:</b> ${app.javaPath}<br/>
                <b>Jarパス:</b> ${app.jarPath}<br/>
                <b>作業ディレクトリ:</b> ${app.workDir}<br/>
                <button onclick="operate('${app.id}', 'start')">起動</button>
                <button onclick="operate('${app.id}', 'stop')">停止</button>
                <button onclick="operate('${app.id}', 'restart')">再起動</button>
                <button onclick="deleteApp('${app.id}')">削除</button>
            `;
            container.appendChild(div);
        }
    }).catch(err => {
        console.error("アプリ一覧取得エラー:", err);
    });
}

function deleteApp(appId) {
    if (!confirm(`アプリ ${appId} を削除しますか？`)) return;

    fetch(`/api/apps/delete?appId=${appId}`, { method: "POST" })
        .then(res => {
            if (!res.ok) throw new Error("削除失敗");
            alert("削除しました");
            loadAppCards();
        })
        .catch(err => {
            alert("エラー: " + err.message);
        });
}



window.onload = () => {
    fetchResource();
    loadAppCards();
};
setInterval(fetchResource, 3000);
setInterval(loadAppCards, 5000);

