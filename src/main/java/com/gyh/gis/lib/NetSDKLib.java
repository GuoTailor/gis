package com.gyh.gis.lib;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.io.Serial;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * NetSDK JNA接口封装
 */
public interface NetSDKLib extends Library {

    NetSDKLib NETSDK_INSTANCE = Native.load(LibraryLoad.getLoadLibrary("dhnetsdk"), NetSDKLib.class);

    class LLong extends IntegerType {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Size of a native long, in bytes.
         */
        public static int size;

        static {
            size = Native.LONG_SIZE;
            if (Utils.getOsPrefix().equalsIgnoreCase("linux-amd64")
                    || Utils.getOsPrefix().equalsIgnoreCase("win32-amd64")
                    || Utils.getOsPrefix().equalsIgnoreCase("mac-64")) {
                size = 8;
            } else if (Utils.getOsPrefix().equalsIgnoreCase("linux-i386")
                    || Utils.getOsPrefix().equalsIgnoreCase("win32-x86")) {
                size = 4;
            }
        }

        /**
         * Create a zero-valued LLong.
         */
        public LLong() {
            this(0);
        }

        /**
         * Create a LLong with the given value.
         */
        public LLong(long value) {
            super(size, value);
        }
    }

    class SdkStructure extends Structure {
        @Override
        protected List<String> getFieldOrder() {
            List<String> fieldOrderList = new ArrayList<>();
            for (Class<?> cls = getClass();
                 !cls.equals(SdkStructure.class);
                 cls = cls.getSuperclass()) {
                Field[] fields = cls.getDeclaredFields();
                int modifiers;
                for (Field field : fields) {
                    modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
                        continue;
                    }
                    fieldOrderList.add(field.getName());
                }
            }
            return fieldOrderList;
        }

        @Override
        public int fieldOffset(String name) {
            return super.fieldOffset(name);
        }
    }

    /************************************************************************
     ** 常量定义
     ***********************************************************************/
    int NET_SERIALNO_LEN = 48;             // 设备序列号字符长度
    int MAX_LOG_PATH_LEN = 260;  // 日志路径名最大长度

    /************************************************************************
     ** 接口
     ***********************************************************************/
    //  JNA直接调用方法定义，cbDisConnect 实际情况并不回调Java代码，仅为定义可以使用如下方式进行定义。 fDisConnect 回调
    boolean CLIENT_Init(Callback cbDisConnect, Pointer dwUser);

    // 设置抓图回调函数, fSnapRev回调
    void CLIENT_SetSnapRevCallBack(Callback OnSnapRevMessage, Pointer dwUser);

    // 抓图请求扩展接口
    boolean CLIENT_SnapPictureEx(LLong lLoginID, SNAP_PARAMS stParam, IntByReference reserved);

    // 打开日志功能
    // pstLogPrintInfo指向LOG_SET_PRINT_INFO的指针
    boolean CLIENT_LogOpen(LOG_SET_PRINT_INFO pstLogPrintInfo);

    //  JNA直接调用方法定义，设置断线重连成功回调函数，设置后SDK内部断线自动重连, fHaveReConnect 回调
    void CLIENT_SetAutoReconnect(Callback cbAutoConnect, Pointer dwUser);

    // 设置连接设备超时时间和尝试次数
    void CLIENT_SetConnectTime(int nWaitTime, int nTryTimes);

    // 设置登陆网络环境
    void CLIENT_SetNetworkParam(NET_PARAM pNetParam);

    // 关闭日志功能
    boolean CLIENT_LogClose();

    //  JNA直接调用方法定义，SDK退出清理
    void CLIENT_Cleanup();

    // 高安全级别登陆
    LLong CLIENT_LoginWithHighLevelSecurity(NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY pstInParam, NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY pstOutParam);

    //  JNA直接调用方法定义，向设备注销
    boolean CLIENT_Logout(LLong lLoginID);

    // 返回函数执行失败代码
    int CLIENT_GetLastError();

    /***********************************************************************
     ** 回调
     ***********************************************************************/
    //JNA Callback方法定义,断线回调
    interface fDisConnect extends StdCallLibrary.StdCallCallback {
        void invoke(LLong lLoginID, String pchDVRIP, int nDVRPort, Pointer dwUser);
    }

    // 抓图回调函数原形(pBuf内存由SDK内部申请释放)
    // EncodeType 编码类型，10：表示jpeg图片      0：mpeg4    CmdSerial : 操作流水号，同步抓图的情况下用不上
    interface fSnapRev extends StdCallLibrary.StdCallCallback {
        void invoke(LLong lLoginID, Pointer pBuf, int RevLen, int EncodeType, int CmdSerial, Pointer dwUser);
    }

    // 网络连接恢复回调函数原形
    interface fHaveReConnect extends StdCallLibrary.StdCallCallback {
        void invoke(LLong lLoginID, String pchDVRIP, int nDVRPort, Pointer dwUser);
    }

    /************************************************************************
     ** 结构体
     ***********************************************************************/
    // 设置登入时的相关参数
    class NET_PARAM extends SdkStructure {
        public int nWaittime;                // 等待超时时间(毫秒为单位)，为0默认5000ms
        public int nConnectTime;                // 连接超时时间(毫秒为单位)，为0默认1500ms
        public int nConnectTryNum;           // 连接尝试次数，为0默认1次
        public int nSubConnectSpaceTime;        // 子连接之间的等待时间(毫秒为单位)，为0默认10ms
        public int nGetDevInfoTime;            // 获取设备信息超时时间，为0默认1000ms
        public int nConnectBufSize;            // 每个连接接收数据缓冲大小(字节为单位)，为0默认250*1024
        public int nGetConnInfoTime;         // 获取子连接信息超时时间(毫秒为单位)，为0默认1000ms
        public int nSearchRecordTime;        // 按时间查询录像文件的超时时间(毫秒为单位),为0默认为3000ms
        public int nsubDisconnetTime;        // 检测子链接断线等待时间(毫秒为单位)，为0默认为60000ms
        public byte byNetType;                // 网络类型, 0-LAN, 1-WAN
        public byte byPlaybackBufSize;        // 回放数据接收缓冲大小（M为单位），为0默认为4M
        public byte bDetectDisconnTime;       // 心跳检测断线时间(单位为秒),为0默认为60s,最小时间为2s
        public byte bKeepLifeInterval;        // 心跳包发送间隔(单位为秒),为0默认为10s,最小间隔为2s
        public int nPicBufSize;              // 实时图片接收缓冲大小（字节为单位），为0默认为2*1024*1024
        public byte[] bReserved = new byte[4];  // 保留字段字段
    }

    // 设备信息扩展///////////////////////////////////////////////////
    class NET_DEVICEINFO_Ex extends SdkStructure {
        public byte[] sSerialNumber = new byte[NET_SERIALNO_LEN];    // 序列号
        public int byAlarmInPortNum;                              // DVR报警输入个数
        public int byAlarmOutPortNum;                             // DVR报警输出个数
        public int byDiskNum;                                     // DVR硬盘个数
        public int byDVRType;                                     // DVR类型,见枚举NET_DEVICE_TYPE
        public int byChanNum;                                     // DVR通道个数
        public byte byLimitLoginTime;                              // 在线超时时间,为0表示不限制登陆,非0表示限制的分钟数
        public byte byLeftLogTimes;                                // 当登陆失败原因为密码错误时,通过此参数通知用户,剩余登陆次数,为0时表示此参数无效
        public byte[] bReserved = new byte[2];                       // 保留字节,字节对齐
        public int byLockLeftTime;                                // 当登陆失败,用户解锁剩余时间（秒数）, -1表示设备未设置该参数
        public byte[] Reserved = new byte[24];                       // 保留
    }

    // 抓图参数结构体
    class SNAP_PARAMS extends SdkStructure {
        public int Channel;                       // 抓图的通道
        public int Quality;                       // 画质；1~6
        public int ImageSize;                     // 画面大小；0：QCIF,1：CIF,2：D1
        public int mode;                          // 抓图模式；-1:表示停止抓图, 0：表示请求一帧, 1：表示定时发送请求, 2：表示连续请求
        public int InterSnap;                     // 时间单位秒；若mode=1表示定时发送请求时
        // 只有部分特殊设备(如：车载设备)支持通过该字段实现定时抓图时间间隔的配置
        // 建议通过 CFG_CMD_ENCODE 配置的stuSnapFormat[nSnapMode].stuVideoFormat.nFrameRate字段实现相关功能
        public int CmdSerial;                     // 请求序列号，有效值范围 0~65535，超过范围会被截断为 unsigned short
        public int[] Reserved = new int[4];
    }

    // SDK全局日志打印信息
    class LOG_SET_PRINT_INFO extends SdkStructure {
        public int dwSize;
        public int bSetFilePath;//是否重设日志路径, BOOL类型，取值0或1
        public byte[] szLogFilePath = new byte[MAX_LOG_PATH_LEN];//日志路径(默认"./sdk_log/sdk_log.log")
        public int bSetFileSize;//是否重设日志文件大小, BOOL类型，取值0或1
        public int nFileSize;//每个日志文件的大小(默认大小10240),单位:比特, 类型为unsigned int
        public int bSetFileNum;//是否重设日志文件个数, BOOL类型，取值0或1
        public int nFileNum;//绕接日志文件个数(默认大小10), 类型为unsigned int
        public int bSetPrintStrategy;//是否重设日志打印输出策略, BOOL类型，取值0或1
        public int nPrintStrategy;//日志输出策略,0:输出到文件(默认); 1:输出到窗口, 类型为unsigned int
        public byte[] byReserved = new byte[4];                            // 字节对齐
        public Pointer cbSDKLogCallBack;                        // 日志回调，需要将sdk日志回调出来时设置，默认为NULL
        public Pointer dwUser;                                    // 用户数据

        public LOG_SET_PRINT_INFO() {
            this.dwSize = this.size();
        }
    }

    // CLIENT_LoginWithHighLevelSecurity 输入参数
    class NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY extends SdkStructure {
        public int dwSize;                            // 结构体大小
        public byte[] szIP = new byte[64];                // IP
        public int nPort;                            // 端口
        public byte[] szUserName = new byte[64];        // 用户名
        public byte[] szPassword = new byte[64];        // 密码
        public int emSpecCap;                        // 登录模式
        public byte[] byReserved = new byte[4];            // 字节对齐
        public Pointer pCapParam;                        // 见 CLIENT_LoginEx 接口 pCapParam 与 nSpecCap 关系
        public int emTLSCap;   //登录的TLS模式，参考EM_LOGIN_TLS_TYPE，目前仅支持EM_LOGIN_SPEC_CAP_TCP，EM_LOGIN_SPEC_CAP_SERVER_CONN 模式下的 tls登陆

        public NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY() {
            this.dwSize = this.size();
        }// 此结构体大小
    }

    // CLIENT_LoginWithHighLevelSecurity 输出参数
    class NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY extends SdkStructure {
        public int dwSize;                            // 结构体大小
        public NET_DEVICEINFO_Ex stuDeviceInfo;                    // 设备信息
        public int nError;                            // 错误码，见 CLIENT_Login 接口错误码
        public byte[] byReserved = new byte[132];        // 预留字段

        public NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY() {
            this.dwSize = this.size();
        }// 此结构体大小
    }
}
