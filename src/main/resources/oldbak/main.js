'use strict';
/* globals MediaRecorder */
navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;

var recBtn = document.querySelector('button#rec');
var pauseResBtn = document.querySelector('button#pauseRes');
var stopBtn = document.querySelector('button#stop');
var startBtn = document.querySelector('button#start');

var videoElement = document.querySelector('video');
var dataElement = document.querySelector('#data');
var downloadLink = document.querySelector('a#downloadLink');

let audioSelect = document.querySelector('select#audioSource');
let videoSelect = document.querySelector('select#videoSource');
let startBtnBoolean = false;
videoElement.controls = false;
/*
 * var mediaSource = new MediaSource();
 * mediaSource.addEventListener('sourceopen', handleSourceOpen, false); var
 * sourceBuffer;
 */
let uid = generateUUID();
let mediaRecorder;
let mediaStream;
let chunks = [];
let count = 0;
//初始化数据参数
let appData;
// let url = "ws://localhost:10080/ws";
let url = "wss://matrix-video.siku.cn/ws";
let constraints = {
    audio: {
        echoCancellation: {exact: true}
    },
    video: {width: {min: 640, max: 1280}, height: {min: 480, max: 720}}
};

let ws;

init();
//获取设备
// navigator.mediaDevices.getUserMedia({
//     video: {width: 640, height: 480}
// });

audioSelect.onchange = getStream;
videoSelect.onchange = getStream;


function generateUUID() {
    let d = new Date().getTime();

    const uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);

    });

    return uuid;
}

/** init websocket **/
function initWs() {
    // var path = 'ws://' + ip + ':' + port + '/ws';
    ws = new WebSocket(url);
    ws.binaryType = "arraybuffer";
}

function reConnection() {
    log('websocket channel error ,will reconnection server !!!');

    startConnect();
    log('Retry websocket Connection channel Success ');

}

/** begin connect **/
function startConnect() {

    if (ws != null) {
        disconnect();
    }

    initParams();

    // init webSocket
    initWs();
    ws.onerror = function () {
        ws = null;
        log('websocket channel error  !!!');
    };

    ws.onclose = function (message) {
        log('websocket channel close');
    };
    ws.onopen = function () {
        log('websocket channel is open,url: ' + url);

        initBusinessParam();
        ws.onmessage = function (event) {
            if (event.type === 'message') {
                log(event.data);
            }
        };
        window.setInterval(function () {
            if (ws != null) {
                const heart = JSON.stringify({
                    uid: uid,
                    type: 0
                })
                // const rqHeart = str2ab(heart);
                // const data = new Uint8Array(rqHeart);
                send(heart)
            }
        }, 3000);
    }
}

function initBusinessParam() {
    const data = JSON.stringify({
        uid: uid,
        type: 2,
        data: appData
    })
    send(data);
}

function str2ab(str) {
    const buf = new ArrayBuffer(str.length * 2 + 1); // 每个字符占用2个字节
    const bufView = new Uint8Array(buf);
    for (var i = 1, strLen = str.length; i < strLen; i++) {
        bufView[i] = str.charCodeAt(i);
    }
    bufView[0] = buf.length;
    return buf;
}

function initParams() {

}

function disconnect() {
    try {
        if (ws != null) {
            ws.close();
            ws = null;
            log('closed ws succ!');
        }
        console.log('Connection was closed successfully!');
    } catch (e) {
        console.log('No connection, please start connect first.');
    }
}

function send(data) {
    //TODO 增加数据前缀
    if (ws != null && ws.readyState == 1) {
        ws.send(data);
    }
}


// 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function () {
    disconnect();
};


// 获取设备
function gotDevices(deviceInfos) {
    console.log(deviceInfos);
    for (let i = 0; i !== deviceInfos.length; ++i) {
        let deviceInfo = deviceInfos[i];
        let option = document.createElement('option');
        option.value = deviceInfo.deviceId;
        // 音频设备
        if (deviceInfo.kind === 'audioinput') {
            option.text = deviceInfo.label || 'microphone ' + (audioSelect.length + 1);
            audioSelect.appendChild(option);
            // 视频设备
        } else if (deviceInfo.kind === 'videoinput') {
            option.text = deviceInfo.label || 'camera ' + (videoSelect.length + 1);
            videoSelect.appendChild(option);
        } else {
            log('Found one other kind of source/device: ' + deviceInfo.label);
        }
    }
}

function handleError(error) {
    console.log('Error: ', error);
}

function getStream() {
    if (window.stream) {
        window.stream.getTracks().forEach(function (track) {
            track.stop();
        });
    }

    constraints = {
        audio: {
            deviceId: {exact: audioSelect.value}
        },
        video: {
            deviceId: {exact: videoSelect.value}
        }
    };

}

function startRecording(stream) {
    mediaStream = stream;
    log('Start recording...');
    if (typeof MediaRecorder.isTypeSupported == 'function') {
        /*
         * MediaRecorder.isTypeSupported is a function announced in
         * https://developers.google.com/web/updates/2016/01/mediarecorder and
         * later introduced in the MediaRecorder API spec
         * http://www.w3.org/TR/mediastream-recording/
         */
        if (MediaRecorder.isTypeSupported('video/webm;codecs=vp9')) {
            var options = {
                mimeType: 'video/webm;codecs=h264'
            };
        } else if (MediaRecorder.isTypeSupported('video/webm;codecs=h264')) {
            var options = {
                mimeType: 'video/webm;codecs=h264'
            };
        } else if (MediaRecorder.isTypeSupported('video/webm;codecs=vp8')) {
            var options = {
                mimeType: 'video/webm;codecs=vp8'
            };
        }
        log('Using mimeType ' + options.mimeType);
        mediaRecorder = new MediaRecorder(stream, options);
    } else {
        log('isTypeSupported is not supported, using default codecs for browser');
        mediaRecorder = new MediaRecorder(stream);
    }

    pauseResBtn.textContent = "暂停";

    mediaRecorder.start(100);

    // var url = window.URL || window.webkitURL;
    // videoElement.src = url ? url.createObjectURL(stream) : stream;
    videoElement.srcObject = stream
    videoElement.play();

    mediaRecorder.ondataavailable = function (e) {
        chunks.push(e.data);
        var reader = new FileReader();
        reader.addEventListener("loadend", function () {
            // reader.result 包含转化为类型数组的blob
            var buf = new Uint8Array(reader.result);

            if (reader.result.byteLength > 0) {        //加这个判断，是因为有很多数据是空的，这个没有必要发到后台服务器，减轻网络开销，提升性能。
                send(buf);
            }
        });
        reader.readAsArrayBuffer(e.data);
    };

    mediaRecorder.onerror = function (e) {
        log('mediaRecorder Error: ' + e);
        console.log('Error: ', e);
    };

    mediaRecorder.onstart = function () {
        log('Started & state = ' + mediaRecorder.state);
    };

    mediaRecorder.onstop = function () {
        log('Stopped  & state = ' + mediaRecorder.state);

        var blob = new Blob(chunks, {
            type: "video/webm"
        });
        chunks.length = 0;

        var videoURL = window.URL.createObjectURL(blob);

        downloadLink.href = videoURL;
        videoElement.src = videoURL;
        downloadLink.innerHTML = '下载视频文件';

        var rand = Math.floor((Math.random() * 10000000));
        var name = "video_" + rand + ".webm";

        downloadLink.setAttribute("download", name);
        downloadLink.setAttribute("name", name);

        closeStream();

        log("停止录制,关闭MediaStream");
    };

    mediaRecorder.onpause = function () {
        log('Paused & state = ' + mediaRecorder.state);
    }

    mediaRecorder.onresume = function () {
        log('Resumed  & state = ' + mediaRecorder.state);
    }

    mediaRecorder.onwarning = function (e) {
        log('Warning: ' + e);
    };
}

// function handleSourceOpen(event) {
// console.log('MediaSource opened');
// sourceBuffer = mediaSource.addSourceBuffer('video/webm; codecs="vp9"');
// console.log('Source buffer: ', sourceBuffer);
// }

function onBtnRecordClicked() {

    const data = JSON.stringify({
        id: Math.floor((Math.random() * 10000000)),
        no: 212453
    })
    start(data);
}

function start(param) {
    appData = param;
    closeStream();

    startConnect();

    if (navigator.mediaDevices.getUserMedia || navigator.getUserMedia) {
        //调用用户媒体设备，访问摄像头
        getUserMedia(constraints, success, error);


        recBtn.disabled = true;
        pauseResBtn.disabled = false;
        stopBtn.disabled = false;
    } else {
        alert("你的浏览器不支持访问用户媒体设备");
    }
}

function closeStream() {
    if (mediaStream != null) {
        mediaStream.getTracks().forEach(track => track.stop());
    }
    disconnect();
}

//访问用户媒体设备的兼容方法
function getUserMedia(constrains, success, error) {
    if (navigator.mediaDevices.getUserMedia) {
        //最新标准API
        log("使用新版API")
        navigator.mediaDevices.getUserMedia(constraints).then(success).catch(error);
    } else if (navigator.getUserMedia) {
        //旧版API
        log("使用旧版本版API")
        navigator.getUserMedia(constraints, success, error);
    }
}

//成功的回调函数
function success(stream) {
    startRecording(stream)
}

//异常的回调函数
function error(error) {
    log("访问用户媒体设备失败：", error.name, error.message);
}


function onBtnStopClicked() {

    if (mediaRecorder == null) {
        closeStream();
        return;
    }
    mediaRecorder.stop();
    videoElement.controls = true;

    recBtn.disabled = false;
    pauseResBtn.disabled = true;
    stopBtn.disabled = true;
    videoElement.srcObject = null;
}

function onPauseResumeClicked() {
    if (pauseResBtn.textContent === "暂停") {
        log("暂停录制");
        pauseResBtn.textContent = "继续";
        mediaRecorder.pause();
        stopBtn.disabled = true;
    } else {
        log("继续录制");
        pauseResBtn.textContent = "暂停";
        mediaRecorder.resume();
        stopBtn.disabled = false;
    }
    recBtn.disabled = true;
    pauseResBtn.disabled = false;
}

function log(message) {
    dataElement.innerHTML = dataElement.innerHTML + '<br>' + message;
}


function init() {
    try {
        navigator.mediaDevices.getUserMedia(constraints).then(ms => {
            if (ms != null) {
                ms.getTracks().forEach(track => track.stop());
            }
            navigator.mediaDevices.enumerateDevices()
                .then(gotDevices).then(getStream).catch(handleError);

        });
    } catch (e) {
        console.error('navigator.getUserMedia error:', e);
        log(`navigator.getUserMedia error:${e.toString()}`);

    }
}


