

- 判断是否存在指定API

      print t.apis;
      def apiInfo = t.apis.admin
      print "2----------------------------"
      print t.apis.adminabc
      print apiInfo.name
      print apiInfo.apis