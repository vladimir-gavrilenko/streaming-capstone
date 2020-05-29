function submitEvent() {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/events', true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.onreadystatechange = function() {
    if (this.readyState === XMLHttpRequest.DONE) {
            console.log('event submitting status: ' + this.status);
        }
    }
    ip = document.getElementById("ip").value;
    console.log(ip)
    xhr.send('ip=' + ip);
}
