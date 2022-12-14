# vue项目创建

## 一、环境准备

1. 安装nodejs

   百度搜索nodejs，进入官网https://nodejs.org/en/
   选择合适的版本下载安装（安装过程没啥要注意的，一路next安装就好），新版的nodejs是自带npm（一个包管理工具，可以让用户从npm服务器下载第三方包到本地使用）的，所以无需额外安装npm

2. 安装好之后验证是否安装成功

   打开cmd命令行输入如下命令**node -v**和**npm -v**，如果输出版本信息则安装成功如下图

   ![image-20221213212320368](C:\Users\tongxin\AppData\Roaming\Typora\typora-user-images\image-20221213212320368.png)

3. 修改模块下载位置，根据个人需要选择是否修改，由于看了网上教程好多都是改了，记录下

   使用npm get prefix查看npm全局模块的存放路径

   ```
   npm get prefix
   ```

   使用npm get cache查看npm缓存默认存放路径

   ```
   npm get cache
   ```

   在nodejs安装目录下创建两个文件夹**node_global** 和 **node_cache**分别存储nodejs下载的全局配置和缓存配置

   更新nodejs的全局模块安装路径和缓存路径，语句如下

   ~~~
   npm config set prefix "你的node_global文件夹路径"
   npm config set cache "你的node_cache文件夹路径"
   ~~~

   设置好后通过上面两个get命令查看配置信息是否更新成功，效果图如下

   ![image-20221213212810491](C:\Users\tongxin\AppData\Roaming\Typora\typora-user-images\image-20221213212810491.png)

   由于 node 全局模块大多数都是可以通过命令行访问的，还要把【node_global】的路径“E:devTools odejs ode_global”加入到【系统变量 】下的【PATH】 变量中，方便直接使用命令行运行

4. 测试以上安装配置是否成功

   通过安装express进行测试，命令如下**npm install express -g**

   安装成功后，可以在**node_global\node_modules**目录下看到下载的文件信息。如果安装过程中出现了操作**node_global** 和 **node_cache**两个文件夹权限不足的问题，右键这两个文件夹点击【属性】，选择【安全】，点击【编辑】，把所有权限勾选上，点击应用即可。

   ![image-20221213213541237](C:\Users\tongxin\AppData\Roaming\Typora\typora-user-images\image-20221213213541237.png)

5. 设置淘宝镜像，由于默认的仓库是国外的，在国内下载速度会很慢，所以更改成淘宝镜像

   ~~~
   npm config set registry https://registry.npm.taobao.org/
   npm config get registry //查看是否设置成功
   ~~~

6. 全局安装基于淘宝源的cnpm，速度会更快

   ~~~
   npm install -g cnpm --registry=https://registry.npm.taobao.org
   ~~~

   验证是否安装成功，打开cmd命令行输入如下命令，出现版本信息即表明安装成功

   ~~~
   cnpm -v
   ~~~

7. 参考文章

   https://blog.csdn.net/m0_67392273/article/details/126113759

   这篇文章讲的很详细



## 二、创建vue项目

这里我选择使用**vue-cli3脚手架安装**

1. 安装脚手架

   ~~~
   npm install -g @vue/cli
   ~~~

   安装之后输入如下命令，出现版本信息即表明安装成功

   ~~~
   vue --version
   ~~~

   ![image-20221213214237324](C:\Users\tongxin\AppData\Roaming\Typora\typora-user-images\image-20221213214237324.png)

2. 创建vue项目

   选择合适的目录执行如下命令

   ~~~
   vue create 你的vue项目名称
   ~~~

   执行后，命令行会提示你如下信息，一般选择第三个，手动选择，按需选择配置，但由于是新手，我就选择第一个默认安装vue3

   ![image-20221213214533576](C:\Users\tongxin\AppData\Roaming\Typora\typora-user-images\image-20221213214533576.png)

   项目创建完成后，提示如下，两个命令，第一个cd命令是用来进入你创建的vue项目的，第二个**npm run serve**是用来启动vue项目

   ![image-20221213215119784](C:\Users\tongxin\AppData\Roaming\Typora\typora-user-images\image-20221213215119784.png)

3. 如果你的vue项目没有下载其他的npm包，可以不执行npm install命令，这个命令是用来管理第三方依赖的，比如你引入了一些依赖，需要执行这个命令来加载一下

4. 进入到你创建的项目目录，执行如下命令启动服务

   ~~~
   npm run serve
   ~~~

   服务起来后提示如下

   ![image-20221213215330878](C:\Users\tongxin\AppData\Roaming\Typora\typora-user-images\image-20221213215330878.png)

5. 根据提示的访问地址访问vue项目

   ![image-20221213215104410](C:\Users\tongxin\AppData\Roaming\Typora\typora-user-images\image-20221213215104410.png)

6. 至此vue项目搭建启动完成，如果想停止vue项目，在命令行界面按**ctrl+c**两次即可停止项目，提示如下，输入y或者n都可以退出批处理操作

   ![image-20221213215357812](C:\Users\tongxin\AppData\Roaming\Typora\typora-user-images\image-20221213215357812.png)

7. 另外一种创建vue项目的方式

   cmd命令行界面输入如下命令（一般命令执行后，会自动跳转到如下页面，或者手动访问localhost:8000），可以通过图形化界面来创建项目

   ~~~
   vue ui
   ~~~

   ![image-20221213215840422](C:\Users\tongxin\AppData\Roaming\Typora\typora-user-images\image-20221213215840422.png)

   ![image-20221213215805483](C:\Users\tongxin\AppData\Roaming\Typora\typora-user-images\image-20221213215805483.png)





**以上仅作为自己学习搭建vue项目的一个记录内容，也是通过百度了各种文档完成的，有问题的话欢迎大家指正**