let maxDate;
let minDate;
let storeNumber;
let lockStores;
let timeFormat = "DD-MM-YY";

let storeId;
let lockStoreId;
let baseUri = "http://localhost:8080/GTA_VI/rest/";


// General Methods
document.getElementById("start-date").addEventListener("change", updateLineCharts);
document.getElementById("end-date").addEventListener("change", updateLineCharts);

buildCharts();

function buildCharts() {
    lockStoreId = true;

    checkToken();

    function checkLockStoreId() {
        if(lockStoreId) {
            window.setTimeout(checkLockStoreId, 100);
        } else {
            updateChartAmount();
            updateChartCategory();
            updateChartValue();
            updateChartDistribution('day');
        }
    }
    checkLockStoreId();
}

function updateDates() {
    let maxDateTemp = document.getElementById("end-date").value;
    maxDate = maxDateTemp.substring(5, 10) + "-21";

    let minDateTemp = document.getElementById("start-date").value;
    minDate = minDateTemp.substring(5, 10) + "-21";
}

function updateLineCharts() {
    updateChartAmount();
    updateChartValue();
}

function checkToken() {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            storeId = this.responseText;
            if (storeId === "false") {
                window.location.href = "login.html";
            }
            lockStoreId = false;
        }
    }
    let uri = baseUri + "login/getId?token=" + localStorage.getItem("token");
    xhr.open("GET", uri);
    xhr.send();
}

function getNumberOfStores() {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            storeNumber = this.responseText;
        }
        lockStores = false;
    }
    let uri = baseUri + "stores/amount";
    xhr.open("GET", uri);
    xhr.send();
}


// Amount Chart
let chartAmount;
let datasetAmount;
let averageAmount;
let lockAmount;
let lockAmountAverage;

function updateChartAmount() {
    lockStores = true;
    lockAmount = true;
    lockAmountAverage = true;

    getNumberOfStores();
    updateDates();
    getDatasetAmount();

    function checkLockStores() {
        if(lockStores) {
            window.setTimeout(checkLockStores, 100);
        } else {
            getAverageAmount();
        }
    }
    checkLockStores();

    function checkLockDataset() {
        // check if data arrived
        if(lockAmount && lockAmountAverage) {
            window.setTimeout(checkLockDataset, 100);
        } else {
            if (typeof chartAmount !== "undefined") {
                chartAmount.destroy();
            }
            createChartAmount();
        }
    }
    checkLockDataset();
}

function getDatasetAmount() {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        let myObj;
        if (this.readyState === 4 && this.status === 200) {
            myObj = JSON.parse(this.responseText);
            datasetAmount = [];
            for (const x in myObj) {
                datasetAmount.push({x: myObj[x].date, y: myObj[x].number});
            }
            lockAmount = false;
        }
    }
    let uri = baseUri + "thefts/amount?start=" + minDate + "&end=" + maxDate + "&store=" + storeId;
    xhr.open("GET", uri);
    xhr.send();
}

function getAverageAmount() {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        let myObj;
        if (this.readyState === 4 && this.status === 200) {
            myObj = JSON.parse(this.responseText);
            averageAmount = [];
            for (const x in myObj) {
                averageAmount.push({x: myObj[x].date, y: myObj[x].number/storeNumber});
            }
            lockAmountAverage = false;
        }
    }
    let uri = baseUri + "thefts/sum?start=" + minDate + "&end=" + maxDate;
    xhr.open("GET", uri);
    xhr.send();
}

function createChartAmount() {
    let ctxAmount = document.getElementById('chart-amount').getContext('2d');
    chartAmount = new Chart(ctxAmount, {
        type: 'line',
        data: {
            datasets: [{
                label: 'Average',
                data: averageAmount,
                borderColor: [
                    'rgba(0,0,0, 0.3)'
                ],
                backgroundColor: ['rgba(0,0,0, 0.3)'],
                borderWidth: 1
            },
                {
                    label: 'Amount',
                    data: datasetAmount,
                    borderColor: [
                        'rgba(255,108,55, 1)'
                    ],
                    backgroundColor: ['rgba(255,108,55, 1)'],
                    borderWidth: 1
                }]
        },
        options: {
            responsive: true,
            title: {
                display: true,
                text: 'Stolen Items per Month'
            },
            scales: {
                xAxes: [{
                    type: "time",
                    time: {
                        format: timeFormat,
                        tooltipFormat: 'll',
                    },
                    gridLines: {
                        display: false
                    },
                    scaleLabel: {
                        display: true,
                        labelString: 'Date'
                    }
                }],
                yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'value'
                    },
                    beginAtZero: true
                }]
            }
        }
    });
}

// Value Chart
let chartValue;
let datasetValue;
let averageValue;
let lockValue;
let lockValueAverage;

function updateChartValue() {
    lockValue = true;
    lockValueAverage = true;
    updateDates();
    getDatasetValue();

    function checkLockStores() {
        if(lockStores) {
            window.setTimeout(checkLockStores, 100);
        } else {
            getAverageValue();
        }
    }
    checkLockStores();

    function checkLockDataset() {
        // check if data arrived
        if(lockAmount && lockValueAverage) {
            window.setTimeout(checkLockDataset, 100);
        } else {
            if (typeof chartValue !== "undefined") {
                chartValue.destroy();
            }
            createChartValue();
        }
    }
    checkLockDataset();
}

function getDatasetValue() {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        let myObj;
        if (this.readyState === 4 && this.status === 200) {
            myObj = JSON.parse(this.responseText);
            datasetValue = [];
            for (const x in myObj) {
                datasetValue.push({x: myObj[x].date, y: myObj[x].number});
            }
            lockValue = false;
        }
    }
    let uri = baseUri + "thefts/value?start=" + minDate + "&end=" + maxDate + "&store=" + storeId;
    xhr.open("GET", uri);
    xhr.send();
}

function getAverageValue() {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        let myObj;
        if (this.readyState === 4 && this.status === 200) {
            myObj = JSON.parse(this.responseText);
            averageValue = [];
            for (const x in myObj) {
                averageValue.push({x: myObj[x].date, y: myObj[x].number/storeNumber});
            }
            lockAmountAverage = false;
        }
    }
    let uri = baseUri + "thefts/value?start=" + minDate + "&end=" + maxDate;
    xhr.open("GET", uri);
    xhr.send();
}

function createChartValue() {
    let ctxValue = document.getElementById('chart-value').getContext('2d');
    chartValue = new Chart(ctxValue, {
        type: 'line',
        data: {
            datasets: [{
                label: 'Average',
                data: averageValue,
                borderColor: [
                    'rgba(0, 0, 0, 0.3)'
                ],
                backgroundColor: ['rgba(0, 0, 0, 0.3)'],
                borderWidth: 1
            },
                {
                    label: 'Value',
                    data: datasetValue,
                    borderColor: [
                        'rgba(0, 0, 100, 1)'
                    ],
                    backgroundColor: ['rgba(0, 0, 100, 1)'],
                    borderWidth: 1
                }]
        },
        options: {
            events: ['click'],
            responsive: true,
            title: {
                display: true,
                text: 'Stolen Items per Month'
            },
            scales: {
                xAxes: [{
                    type: "time",
                    time: {
                        format: timeFormat,
                        tooltipFormat: 'll',
                    },
                    gridLines: {
                        display: false
                    },
                    scaleLabel: {
                        display: true,
                        labelString: 'Date'
                    }
                }],
                yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'value'
                    },
                    beginAtZero: true
                }]
            }
        }
    });
}


// Category Chart
let chartCategory;

function updateChartCategory() {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        let myObj;
        if (this.readyState === 4 && this.status === 200) {
            myObj = JSON.parse(this.responseText);
            let datasetCategory = [];
            let labelsCategory = [];
            for (const x in myObj) {
                datasetCategory.push(myObj[x].number);
                labelsCategory.push("Category " + myObj[x].category)
            }

            if (typeof chartCategory !== "undefined") {
                chartCategory.destroy();
            }
            createChartCategory(labelsCategory, datasetCategory);

        }
    }
    let uri = baseUri + "thefts/categories?store=" + storeId;
    xhr.open("GET", uri);
    xhr.send();
}

function createChartCategory(labelsCategory, datasetCategory) {
    let ctxCategory = document.getElementById('chart-categories').getContext('2d');
    chart = new Chart(ctxCategory, {
        type: 'pie',
        data: {
            labels: labelsCategory,
            datasets: [
                {
                    label: "Losses per category",
                    backgroundColor: ["#3e95cd", "#8e5ea2", "#3cba9f", "#e8c3b9", "#c45850"],
                    data: datasetCategory
                }
            ]
        },
        options: {
            title: {
                display: true,
                text: 'Losses in each product category'
            }
        }
    });
}


// Weekly Chart
let chartDistribution;
let distribution = 0;
let weekdays = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];

document.getElementById("duration").addEventListener("change", changeDistribution);

function updateChartDistribution(selection) {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        let myObj;
        if (this.readyState === 4 && this.status === 200) {
            myObj = JSON.parse(this.responseText);
            let datasetWeekly = [];
            for (const x in myObj) {
                switch (distribution) {
                    case 0:
                        datasetWeekly.push({x: myObj[x].date + ":00", y: myObj[x].number});
                        break;
                    case 1:
                        datasetWeekly.push({x: weekdays[x], y: myObj[x].number});
                        break;
                    case 2:
                        datasetWeekly.push({x: myObj[x].date, y: myObj[x].number});
                        break;
                }
            }

            if (typeof chartDistribution !== "undefined") {
                chartDistribution.destroy();
            }
            createChartDistribution(datasetWeekly);
        }
    }

    let uri = baseUri;
    switch (selection) {
        case 'day':
            uri += "thefts/day?store=" + storeId;
            break;
        case 'week':
            uri += "thefts/week?store=" + storeId;
            break;
        case 'month':
            uri += "thefts/month?store=" + storeId;
            break;
    }

    xhr.open("GET", uri);
    xhr.send();
}

function createChartDistribution(datasetDistribution) {
    let ctxDistribution = document.getElementById('chart-distribution').getContext('2d');
    chartDistribution = new Chart(ctxDistribution, {
        type: 'line',
        data: {
            datasets: [{
                label: 'Value',
                data: datasetDistribution,
                borderColor: [
                    'rgba(0, 0, 100, 1)'
                ],
                backgroundColor: ['rgba(0, 0, 100, 1)'],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            title: {
                display: true,
                text: 'Stolen Items per Month'
            },
            scales: {
                xAxes: [{

                    gridLines: {
                        display: false
                    },
                    scaleLabel: {
                        display: true,
                        labelString: 'Date'
                    }
                }],
                yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'value'
                    },
                    beginAtZero: true
                }]
            }
        }
    });
}

function changeDistribution() {
    let e = document.getElementById("duration");
    let selection = e.options[e.selectedIndex].value;
    switch (selection) {
        case 'day':
            distribution = 0;
            break;
        case 'week':
            distribution = 1;
            break;
        case 'month':
            distribution = 2;
            break;
    }
    updateChartDistribution(selection);
}


document.querySelectorAll(".chart").forEach(chrt => chrt.addEventListener("click", openInModal(chrt)));

function openInModal(chart) {
    let modal = $("chartModal");
    //modal.empty();
    //modal.append("<h2>Graph</h2>");
    //modal.append(chart.innerHTML);
    modal.modal();
}
