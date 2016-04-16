/**
 * Created by IntelliJ IDEA.
 * User: zzh
 * Date: Feb 28, 2004
 * Time: 4:44:33 PM
 * To change this template use Options | File Templates.
 */
package com.inetec.ichange.main.api;


import com.inetec.common.config.nodes.Plugin;
import com.inetec.common.exception.Ex;

import java.io.InputStream;

/**
 * 输入适配器接口，输入适配器必须实现它。
 * 平台与适配器之间的协议如下：
 * 1. 平台实例化输入与输出适配器的类对象。输入与输出适配器之间
 * 不分先后顺序。
 * 2. 平台调用适配器的init方法。输入与输出适配器之间
 * 不分先后顺序。
 * 3. 平台调用适配器的config方法。
 * 4. 平台调用输入适配器的start方法。
 *
 * @author inetec System
 * @version 1.0
 */

public interface ISourePlugin {
    /**
     * 初始化输入适配器
     *
     * @param frp  是与输入适配器对应的输出适配器， 这个参
     *             数，使的同一类型的输出适配器与输入适配器之间能够
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
     * 通过控制通道控制当前应用（输入适配器）
     *
     * @param command 可用的命令: disconnect, controlwrite, or getstatus; or
     *                other customer control command
     * @param fp      执行控制命令的属性对象。
     * @throws Ex
     */
    DataAttributes control(String command, DataAttributes fp) throws Ex;

    /**
     * 输入适配外部数据方法。实现允许符合相应格式的数据通过输
     * 入适配器传到平台。
     *
     * @param in 数据流。在输入适配器之外组装成的。
     * @param fp 可能的值: 空，新对象或着控制命令.
     * @throws Ex 当有错误发生时。
     */
    DataAttributes externalData(InputStream in, DataAttributes fp) throws Ex;

    public void config(Plugin plugin) throws Ex;

    public boolean configred();
}
