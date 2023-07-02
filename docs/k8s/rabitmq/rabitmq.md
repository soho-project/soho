## 安装依赖环境

    kubectl apply -f "https://github.com/rabbitmq/cluster-operator/releases/latest/download/cluster-operator.yml"
    //or 安装本目录的 rabbitmq.yaml

## 创建rabitmq集群

    kubectl rabbitmq tail hello-world

## 获取用户名密码

    username="$(kubectl get secret hello-world-default-user -o jsonpath='{.data.username}' | base64 --decode)"
    echo "username: $username"
    password="$(kubectl get secret hello-world-default-user -o jsonpath='{.data.password}' | base64 --decode)"
    echo "password: $password"

## 转发服务端口

    kubectl port-forward "service/hello-world" 15672
