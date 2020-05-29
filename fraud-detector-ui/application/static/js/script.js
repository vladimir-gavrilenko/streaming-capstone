function submitEvents() {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/events', true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function() {
        console.log('event submitting status: ' + xhr.status);
    }
    ips = document.getElementById('ips').value;
    console.log(ips);
    xhr.send('ips=' + ips);
}

function displayBots() {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/bots', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function() {
        var bots = JSON.parse(xhr.responseText)['bots'];
        console.log(bots);
        var botsElement = document.getElementById('active-bots');
        botsElement.innerHTML = ''
        for (var i = 0; i < bots.length; i++) {
            var botElement = document.createElement('li');
            var botElementText = document.createTextNode(bots[i]);
            botElement.appendChild(botElementText);
            botsElement.append(botElement);
        }
    };
    xhr.send(null);
}

function main() {
    console.log('main')
    setInterval(displayBots, 2000);
}
