package com.inetec.ichange.main.api;


import com.inetec.common.config.nodes.IChange;
import com.inetec.common.exception.Ex;

import java.io.InputStream;

/**
 * 输出适配器第二版。平台接收数据后调用对应的输出适配器。完成数据的后处理。
 *
 * @author inetec System
 * @version 1.0
 */
public interface ITargetPlugin {
    /**
     * 测定这个输出适配器对接收到的数据的后处理能力。后处理方式
     * 分为数据流和文件两种。这个方法要告诉平台这个输出适配器
     * 有什么样的数据处理能力。
     *
     * @return 返回值为 1采用流处理方式，返回值为2采用文件处
     *         理方式。返回值为3表示这个输出适配器具有以上两种处理能力。
     */
    public int[] getCapabilities();

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
    void init(IChangeMain dc, IChangeType type, ISourcePlugin frp) throws Ex;
    /**
     * 处理: 处理输入文件 (inFilename).
     * 在返回之前，更新DataAttributes对象中文件类型和文件名的值
     * 返回值说明: 处理结果对象，包括处理状态，处理数据。
     */
    /**
     * 表示能够处理流形式的数据。
     */
    public static final int I_StreamCapability = 1;
    /**
     * 表示能够处理文件形式的数据
     */
    public static final int I_FileCapability = 2;

    public DataAttributes process(String collectionType
            , DataAttributes props
            , String inFilename) throws Ex;

    /**
     * 处理数据流
     *
     * @param collectionType 采集类型
     * @param props          数据控告属性
     * @param is             数据输入流，
     * @return 请参看类似方法的说明。
     * @throws Ex 当有错误发生，
     */
    public DataAttributes process(String collectionType
            , DataAttributes props
            , InputStream is) throws Ex;

    /**
     * 通过控制通道控制当前应用（这个输出适配器）
     *
     * @param command 可用的命令: disconnect, controlwrite, or getstatus; or
     *                other customer control command
     * @param fp      执行控制命令的属性集合。
     * @throws Ex 当有错误出现时
     */
    DataAttributes control(String command, DataAttributes fp) throws Ex;

    public void config(IChange ichange) throws Ex;

    public boolean configred();

}
