# AccessPCShareFile
工程是依赖jcifs，Android下访问局域网PC上的共享文件,将jcifs.jar包添加到工程目录里，即可使用。
jcifs开源项目，官网：https://jcifs.samba.org

jcifs是samba组织下的一帮牛开发的一套兼容SMB协议的library，我们可以用它来在java里访问Windows共享文件；
通过该jar里的方法能够获取局域网内的windowsPC的名称、共享文件夹和文件;
手机和苹果的设备不能获取，因为该类使用的是IBM早期的NETBIOS协议，无法读取现有设备上的数值。

