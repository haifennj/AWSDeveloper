# AWS开发者插件

作者：
**Haiifenng**

## 使用方法

下载[AWSDeveloper_1.0.0.201807151540.jar](https://github.com/haiifenng/AWSDeveloper/releases/download/1.0.0/AWSDeveloper_1.0.0.201807151540.jar)文件。

部署位置：
* Windows：eclipse/dropins
* macOS：应用程序-Eclipse，右键-显示包内容，Contents/Eclipse/dropins

重启Eclipse。

## 功能
### 自动创建及更新AWS需要的Libraries

* 右键点击任意工程目录，选择`Refresh`菜单，自动创建`aws_lib`的库并且更新release下需要的jar包

* 同时，可以一键更新AWS的库和依赖文件

> 功能入口：右键点击任意工程目录-`Refresh`

### AWS应用快速管理

#### `apps`的资源快速软连接

AWS资源使用新的管理方式后，App的资源代码使用独立的Git库管理，和release分开了，插件提供了两种菜单：
* `Link App` ：仅仅使用软连接的形式部署到release资源中，方便使用该App。如果已经软连接，则会显示Already Linked。

**限制条件：**

需要导入名称为`apps`的工程，该工程是所有App的资源文件

**注意**

选择菜单操作之前，首先在工作空间中导入`release`工程


