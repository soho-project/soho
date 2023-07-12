长链接模块
=========


默认连接地址:  http://IP:8080/wz

通信交互：

- 认证

        //发送
        eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2OTA0ODk0NTgsImlhdCI6MTY4OTE3NTQ1OH0.eAoMkpnIi74atazzi3Ag4Qrw7yFKBekNbg_h0VkV7sgFG9iHuOi7XRV24-j7unGc_J51YxifC_wdgIocjuw7Pg

       //成功
        +OK 
       //失败
       +ERR

- 接收客户端消息

        //客户端接收消息使用事件监听
        work.soho.longlink.api.event.MessageEvent
        

- 服务器端发送消息到客户端

        //发送消息 使用bean work.soho.longlink.api.sender.Sender
        void sendToUid(String uid, String msg);
        void sendToConnectId(String connectId, String msg);
        void sendToAllUid(String msg);
        void sendToAllConnect(String msg);
        void bindUid(String connectId, String uid);
