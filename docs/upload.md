上传
===

    upload:
        enable: true
        defaultChannel: oss
        # ali oss upload setting
        oss:
            enable: true
            type: oss
            config:
                endpoint: oss-cn-shenzhen.aliyuncs.com
                accessKeyId: xxxx
                accessKeySecret: xxxx
                bucketName: xxxx
                urlPrefix: https://xxxx.aliyuncs.com/ 
        # 阿里云上传2 配置
        oss2:
            enable: true
            type: oss
            config:
                endpoint: oss-cn-shenzhen.aliyuncs.com
                accessKeyId: xxxx
                accessKeySecret: xxxx
                bucketName: xxxx
                urlPrefix: https://xxxx.aliyuncs.com/
        # 腾讯cos上传配置
        cos:
            type: cos
            enable: true
            config:
                secretId: xxxx
                secretKey: xxxx
                region: ap-guangzhou
                bucketName: soho-admin-demo-1258009624
                urlPrefix: https://xxxx.file.myqcloud.com/
        # 七牛存储配置
        qiniu:
            type: qiniu
            enable: true
            config:
                accessKey: xxx
                secretKey: xxxx
                bucket: xxxx
                urlPrefix: http://ra86b7o3s.hn-bkt.clouddn.com/
        file:
          type: file
          enable: true
          config:
            baseDir: D:\data\
            urlPrefix: http://ra86b7o3s.hn-bkt.clouddn.com/
        smb:
          type: smb
          enable: true
          config:
            hostname: [主机名]
            username: [用户名]
            password: [密码]
            shareName: [共享明]
            domain: DOMAIN
            urlPrefix: http://res.liufang.org.cn/
            pathPrefix: [路径前缀，可为空]

使用用例
=======

    //上传到默认通道； 取决于配置项： upload.defaultChannel 配置
    UploadUtils.upload("test/utils.txt", "hello utils");
    UploadUtils.upload("oss", "test/utils.txt", "hello utils");
    UploadUtils.upload("oss2", "test/utils.txt", "hello utils");
    UploadUtils.upload("cos", "test/utils.txt", "hello utils");
