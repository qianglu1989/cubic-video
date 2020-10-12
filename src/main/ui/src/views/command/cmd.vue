<template>
    <div id="vueapp">
        <div style="text-align:center;">

            <h2>视频录制建议使用Chrome浏览器</h2>
            <label>Audio source: </label>
            <el-select v-model="audioValue" placeholder="请选择" @change="audioChange">
                <el-option
                        v-for="item in audioOptions"
                        :key="item.get('value')"
                        :label="item.get('label')"
                        :value="item.get('value')">
                </el-option>
            </el-select>
            <label>Video source: </label>
            <el-select v-model="videoValue" placeholder="请选择" @change="videoChange">
                <el-option
                        v-for="item in videoOptions"
                        :key="item.get('value')"
                        :label="item.get('label')"
                        :value="item.get('value')">
                </el-option>
            </el-select>


            <video id="my-player" meted="meted" autoplay
                   style="clear:both;display:block;margin:auto;width: 640px;height: 480px"></video>
            <!--            <el-button id="openVideo" :plain="true" type="success" :disabled="openVideo" @click.native="openRec">打开摄像头-->
            <!--            </el-button>-->
            <el-button id="rec" :plain="true" type="success" :disabled="recBtn" @click.native="startRec">录制</el-button>
            <el-button id="pauseRec" :plain="true" type="warning" :disabled="pauseResBtn" @click.native="pauseRec">
                {{pause?'继续':'暂停'}}
            </el-button>
            <el-button id="stopRec" :plain="true" type="danger" :disabled="stopBtn" @click.native="stopRec">停止
            </el-button>
            <el-button id="saveRec" :plain="true" type="primary" :disabled="saveBtn" @click.native="saveVideo">保持本地视频
            </el-button>

            <el-button id="getRec" :plain="true" type="primary" :disabled="getBtn" @click.native="getVideo">获取远程文件
            </el-button>
            <br/>
            <br/>
            <p id="data"></p>
        </div>

    </div>
</template>

<script>
    import axios from 'axios'


    export default {
        name: 'HelloWorld',
        props: {
            msg: String
        },
        data() {

            return {
                dataElement: null,
                audioSelect: 0,
                videoSelect: 0,
                uid: this.generateUUID(),
                mr: null,
                chunks: [],
                url: "wss://matrix-video.siku.cn/ws",
                restUrl: "https://gw.siku.cn/video/getVideo?id=",
                // restUrl: "https://gw-test.siku.cn/video/getVideo?id=",
                // restUrl: "http://localhost:6080/video/getVideo?id=",
                // url: "ws://localhost:10080/ws",
                ws: null,
                videoOptions: [],
                audioOptions: [],
                videoValue: '',
                audioValue: '',
                textarea: '',
                constraints: {
                    audio: {
                        echoCancellation: {exact: true},
                        deviceId: {exact: this.audioValue}
                    },
                    video: {
                        width: {
                            min: 1280,
                            max: 1920
                        },
                        height: {
                            min: 720,
                            max: 1080
                        },
                        deviceId: {
                            exact: this.videoValue
                        }
                    }

                },
                params: {
                    "id": Math.floor((Math.random() * 10000000))
                },
                recBtn: false,
                pauseResBtn: true,
                pause: false,
                stopBtn: true,
                openVideo: false,
                getBtn: true,
                saveBtn: true,
                stream: null,
                ply: null,
                options: {},
                mediaOptions: {},
                videoElement: null,
                saveUrl: ''
            }
        },
        mounted() {
            this.init();
            this.openRec();
        },
        computed: {},
        methods: {
            saveVideo() {
                const name = "video_" + this.params.id + ".webm";

                let a = document.createElement('a');
                a.href = this.saveUrl;
                a.download = name;
                a.click()
            },
            getVideo() {
                axios.get(this.restUrl + this.params.id)
                    .then(response => {
                        const url = response.data;
                        if (url != '') {
                            window.open(url, '_blank');
                        } else {
                            this.$message({
                                showClose: true,
                                message: '视频正在处理中，请稍后。。。',
                                type: 'success'
                            });
                        }
                    })
            },
            log(message) {

                this.dataElement.innerHTML = this.dataElement.innerHTML + '<br>' + message;
                // console.log("LOG 》》" + message)
            },
            stopRecorder() {

                if (this.mr == null) {
                    return;
                }
                this.mr.stop();
                this.mr = null;
            },
            error(error) {
                this.log("访问用户媒体设备失败：", error.name, error.message);
            },
            closeStream() {
                if (this.stream != null) {
                    this.stream.getTracks().forEach(track => track.stop());
                    this.stream = null;
                }
                this.disconnect();
            },
            startRecording() {
                const that = this;
                this.log('Start recording...');
                if (typeof MediaRecorder.isTypeSupported == 'function') {
                    /*
                     * MediaRecorder.isTypeSupported is a function announced in
                     * https://developers.google.com/web/updates/2016/01/mediarecorder and
                     * later introduced in the MediaRecorder API spec
                     * http://www.w3.org/TR/mediastream-recording/
                     */
                    if (MediaRecorder.isTypeSupported('video/webm;codecs=vp9')) {
                        this.mediaOptions = {
                            mimeType: 'video/webm;codecs=h264'
                        };
                    } else if (MediaRecorder.isTypeSupported('video/webm;codecs=h264')) {
                        this.mediaOptions = {
                            mimeType: 'video/webm;codecs=h264'
                        };
                    } else if (MediaRecorder.isTypeSupported('video/webm;codecs=vp8')) {
                        this.mediaOptions = {
                            mimeType: 'video/webm;codecs=vp8'
                        };
                    }
                    this.log('Using mimeType ' + this.mediaOptions.mimeType);
                    this.mr = new MediaRecorder(this.stream, this.mediaOptions);
                } else {
                    this.log('isTypeSupported is not supported, using default codecs for browser');
                    this.mr = new MediaRecorder(this.stream);
                }
                this.mr.start(100);


                this.mr.ondataavailable = function (e) {
                    that.chunks.push(e.data);
                    var reader = new FileReader();
                    reader.addEventListener("loadend", function () {
                        // reader.result 包含转化为类型数组的blob
                        var buf = new Uint8Array(reader.result);

                        if (reader.result.byteLength > 0) {        //加这个判断，是因为有很多数据是空的，这个没有必要发到后台服务器，减轻网络开销，提升性能。
                            that.send(buf);
                        }
                    });
                    reader.readAsArrayBuffer(e.data);
                };

                this.mr.onerror = function (e) {
                    that.log('mediaRecorder Error: ' + e);
                    console.log('Error: ', e);
                };

                this.mr.onstart = function () {
                    that.log('Started & state = ' + that.mr.state);
                };

                this.mr.onstop = function () {
                    var blob = new Blob(that.chunks, {
                        type: "video/webm"
                    });
                    that.chunks.length = 0;

                    var videoURL = window.URL.createObjectURL(blob);

                    that.saveUrl = videoURL;
                    // videoElement.src = videoURL;


                    // closeStream(stream);

                    that.log("停止录制,关闭MediaStream");
                };

                this.mr.onpause = function () {
                    that.log('Paused & state = ' + that.mr.state);
                }

                this.mr.onresume = function () {
                    that.log('Resumed  & state = ' + that.mr.state);
                }

                this.mr.onwarning = function (e) {
                    that.log('Warning: ' + e);
                };
            },
            handleError(error) {
                console.log('Error: ', error);
            },
            gotDevices(deviceInfos) {
                const vos = new Array();
                const aos = new Array();
                console.log(deviceInfos);
                for (let i = 0; i !== deviceInfos.length; ++i) {
                    const op = new Map();
                    let deviceInfo = deviceInfos[i];
                    op.set("value", deviceInfo.deviceId);
                    // 音频设备
                    if (deviceInfo.kind === 'audioinput') {
                        op.set("label", deviceInfo.label || 'microphone ' + (this.audioSelect + 1));
                        aos.push(op);
                        // 视频设备
                    } else if (deviceInfo.kind === 'videoinput') {
                        op.set("label", deviceInfo.label || 'microphone ' + (this.videoSelect + 1));
                        vos.push(op);
                    } else {
                        this.log('Found one other kind of source/device: ' + deviceInfo.label);
                    }
                }
                this.audioOptions = aos;
                this.videoOptions = vos;
                this.audioValue = aos[0].get("label");
                this.videoValue = vos[0].get("label");
                this.constraints.audio.deviceId.exact = aos[0].get("value");
                this.constraints.video.deviceId.exact = vos[0].get("value");
            },
            send(data) {
                //TODO 增加数据前缀
                if (this.ws != null && this.ws.readyState == 1) {
                    this.ws.send(data);
                }
            },
            disconnect() {
                try {
                    if (this.ws != null) {
                        this.ws.close();
                        this.ws = null;
                        this.log('closed ws succ!');
                    }
                    console.log('Connection was closed successfully!');
                } catch (e) {
                    console.log('No connection, please start connect first.');
                }
            },
            stopVideo() {
                this.disconnect();
                this.stopRecorder();

            },
            initParams() {

            },
            initBusinessParam() {
                const data = JSON.stringify({
                    uid: this.uid,
                    type: 2,
                    data: JSON.stringify(this.params)
                })
                this.send(data);
            },
            startConnect() {
                const that = this;
                if (this.ws != null) {
                    this.disconnect();
                }

                this.initParams();

                // init webSocket
                this.initWs();
                this.ws.onerror = function () {
                    that.$confirm('数据传输出现问题 , 是否继续? 如继续录制视频将保存在本地，将不能自动上传服务器', '提示', {
                        confirmButtonText: '继续本地录制',
                        cancelButtonText: '取消录制',
                        type: 'warning'
                    }).then(() => {
                        that.$message({
                            type: 'warning',
                            message: '继续录制，录制完后请自行保存本地视频!'
                        });
                    }).catch(() => {
                        that.stopRec();
                        that.$message({
                            type: 'warning',
                            message: '已取消录制'
                        });
                    });

                };

                this.ws.onclose = function (message) {
                    that.log('websocket channel close' + message);
                };
                this.ws.onopen = function () {
                    that.log('websocket channel is open,url: ' + this.url);

                    that.initBusinessParam();
                    that.ws.onmessage = function (event) {
                        if (event.type === 'message') {
                            that.log(event.data);
                        }
                    };
                    window.setInterval(function () {
                        if (that.ws != null) {
                            const heart = JSON.stringify({
                                uid: that.uid,
                                type: 0
                            })
                            // const rqHeart = str2ab(heart);
                            // const data = new Uint8Array(rqHeart);
                            that.send(heart)
                        }
                    }, 30000);
                }
            },
            initWs() {
                // var path = 'ws://' + ip + ':' + port + '/ws';
                this.ws = new WebSocket(this.url);
                this.ws.binaryType = "arraybuffer";
            },
            generateUUID() {
                let d = new Date().getTime();

                const uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                    const r = (d + Math.random() * 16) % 16 | 0;
                    d = Math.floor(d / 16);
                    return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);

                });

                return uuid;
            },
            openRec() {
                let that = this;

                if (that.stream != null) {
                    this.$message({
                        showClose: true,
                        message: '摄像头已开启',
                        type: 'success'
                    });
                    return;
                }
                this.player();
                this.$message({
                    showClose: true,
                    message: '摄像头已开启',
                    type: 'success'
                });
            },
            pauseRec() {
                if (!this.pause) {
                    this.pause = true;
                    this.log("暂停录制");
                    this.mr.pause();
                    this.$message({
                        showClose: true,
                        message: '暂停录制',
                        type: 'warning'
                    });
                } else {
                    this.pause = false;
                    this.stopBtn = false;
                    this.log("继续录制");
                    this.mr.resume();
                    this.$message({
                        showClose: true,
                        message: '继续录制',
                        type: 'success'
                    });
                }
                this.recBtn = true;
                this.pauseResBtn = false;

            },
            stopRec() {
                this.stopVideo();
                this.saveBtn = false;
                this.getBtn = false;
                this.recBtn = false;
                this.pauseResBtn = true;
                this.stopBtn = true;
                this.$message({
                    showClose: true,
                    message: '停止录制',
                    type: 'warning'
                });
            },
            startRec() {
                this.dataElement.innerHTML = 'startRec...';
                this.openRec();
                this.saveBtn = true;
                this.getBtn = true;
                this.recBtn = true;
                this.pauseResBtn = false;
                this.stopBtn = false;
                this.startConnect();
                this.startRecording();
                this.$message({
                    showClose: true,
                    message: '开始录制',
                    type: 'success'
                });
            },
            player() {
                let that = this;
                // const pl = Video('my-player', this.options);

                navigator.mediaDevices.getUserMedia(that.constraints).then(function (ms) {
                    // const vid = pl.tech().el();
                    // vid.srcObject = ms;
                    // that.stream = ms;
                    // that.ply = pl
                    that.stream =ms;
                    const videoElement = document.querySelector('video');
                    videoElement.srcObject = ms;
                    videoElement.play();
                    that.ply = videoElement;
                });
            },
            reOpenPlayer() {
                this.closeStream();
                this.stopRecorder();
                this.openRec();
            },
            audioChange(data) {
                let that = this;

                this.audioValue = data;
                that.constraints.audio.deviceId.exact = data;
                this.reOpenPlayer();
                this.$message({
                    showClose: true,
                    message: '切换音频成功，id: ' + data,
                    type: 'success'
                });
            },
            videoChange(data) {
                let that = this;

                this.videoValue = data;
                that.constraints.video.deviceId.exact = data;
                this.reOpenPlayer();
                this.$message({
                    showClose: true,
                    message: '切换视频成功，id: ' + data,
                    type: 'success'
                });
            },
            init() {
                try {
                    this.dataElement = document.querySelector('#data');

                    this.videoElement = document.querySelector('video');

                    navigator.mediaDevices.getUserMedia(this.constraints).then(ms => {
                        if (ms != null) {
                            ms.getTracks().forEach(track => track.stop());
                        }
                        navigator.mediaDevices.enumerateDevices()
                            .then(this.gotDevices);
                    });
                } catch (e) {
                    console.error('navigator.getUserMedia error:', e);
                    this.log(`navigator.getUserMedia error:${e.toString()}`);

                }
            }
        }
    }
</script>
<style>
    a#downloadLink {
        display: block;
        margin: 0 0 1em 0;
        min-height: 1.2em;
    }

    p#data {
        min-height: 6em;
    }
</style>
