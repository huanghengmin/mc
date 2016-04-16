package com.inetec.ichange.main.api;


import com.inetec.common.config.nodes.IChange;
import com.inetec.common.exception.Ex;

import java.io.InputStream;

/**
 * 源插入件接口，源插入件接口必须实现它。
 * 平台与插入件接口之间的协议如下：
 * 1. 平台实例化输入与输出插入件接口的类对象。输入与输出插入件之
 * 不分先后顺序。
 * 2. 平台调用插入件的init方法。输入与输出插入件之间
 * 不分先后顺序。
 * 3. 平台调用插入件的config方法。
 * 4. 平台调用源插入件的start方法。
 *
 * @author inetec System
 * @version 1.0
 */

public interface ISourcePlugin {
    /**
     * 初始化输入适配器
     *
     * @param frp  是与源插入件对应的目标插入件， 这个参
     *             数，使的同一类型的目标插入件与源插入件之间能够
     *             进行的通信。.
     * @param type 是平台用来标识数据的关建字。
     * @param dc   采集接口，定义了数据传输方法，控制命令
     * @throws Ex 当有错误的时候。
     */
    void init(IChangeMain dc, IChangeType type, ITargetPlugin frp) throws Ex;

    /**
     * 开始数据处理操作。
     *
     * @param template 当平台调用start方法时，要新建一个
     *                 DataAttributes对象。
     * @throws Ex 当有错误出现的时候。
     */
    DataAttributes start(DataAttributes template) throws Ex;

    /**
     * 停止处理数据。只有start方法成功执行后，才能执行stop方法。
     *
     * @throws Ex
     */
    DataAttributes stop() throws Ex;

    /**
     * 通过控制通道控制当前应用（源插入件）
     *
     * @param command 可用的命令: disconnect, controlwrite, or getstatus; or
     *                other customer control command
     * @param fp      执行控制命令的属性对象。
     * @throws Ex
     */
    DataAttributes control(String command, DataAttributes fp) throws Ex;

    /**
     * 源插入件外部数据方法。实现允许符合相应格式的数据通过输
     * 入适配器传到平台。
     *
     * @param in 数据流。在源插入件之外组装成的。
     * @param fp 可能的值: 空，新对象或着控制命令.
     * @throws Ex 当有错误发生时。
     */
    DataAttributes externalData(InputStream in, DataAttributes fp) throws Ex;

    public void config(IChange change) throws Ex;

    public boolean configred();
}
