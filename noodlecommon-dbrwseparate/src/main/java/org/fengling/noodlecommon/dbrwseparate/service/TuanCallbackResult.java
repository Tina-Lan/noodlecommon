
package org.fengling.noodlecommon.dbrwseparate.service;


public class TuanCallbackResult {
	
	public final static int SUCCESS = 1;
	public final static int FAILURE = -1;

	private int statusCode = SUCCESS;
	private int resultCode = 0;

	private Throwable throwable;
	private Object businessObject;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Object getBusinessObject() {
        return businessObject;
    }

    public void setBusinessObject(Object businessObject) {
        this.businessObject = businessObject;
    }

    /**
     * ��ֹ�û�ֱ�ӹ���ö��󣬱���ͨ��ҵ�񷽷�����
     */
    private TuanCallbackResult(int statusCode, int resultCode, Throwable throwable, Object businessObject) {
        this.statusCode = statusCode;
        this.resultCode = resultCode;
        this.throwable = throwable;
        this.businessObject = businessObject;
    }

    //------------------ �������� --------------------------------------

    /**
     * ֱ�ӹ���ɹ�״���µĻص��������
     */
    public static TuanCallbackResult success() {
        return success(0, null);
    }

    /**
     * ֱ�ӹ���ɹ�״���µĻص��������ͬʱ����ҵ�����
     */
    public static TuanCallbackResult success(int resultCode) {
        return success(resultCode, null);
    }

    /**
     * ֱ�ӹ���ɹ�״���µĻص��������ͬʱ����ҵ����롢ҵ�����
     */
    public static TuanCallbackResult success(int resultCode, Object businessObject) {
        return new TuanCallbackResult(SUCCESS, resultCode, null, businessObject);
    }

    /**
     * ֱ�ӹ���ʧ��״���µĻص��������ͬʱ����ҵ�����
     */
    public static TuanCallbackResult failure(int resultCode) {
        return failure(resultCode, null, null);
    }

    /**
     * ֱ�ӹ���ʧ��״���µĻص��������ͬʱ����ҵ����롢�쳣
     */
    public static TuanCallbackResult failure(int resultCode, Throwable throwable) {
        return failure(resultCode, throwable, null);
    }

    /**
     * ֱ�ӹ���ʧ��״���µĻص��������ͬʱ����ҵ����롢�쳣��ҵ�����
     */
    public static TuanCallbackResult failure(int resultCode, Throwable throwable,Object businessObject) {
        return new TuanCallbackResult(FAILURE, resultCode, throwable, businessObject);
    }

    /**
     * ���������Ƿ�ɹ�
     */
    public boolean isSuccess() {
        return statusCode == SUCCESS;
    }

    /**
     * ���������Ƿ�ʧ��
     */
    public boolean isFailure() {
        return statusCode == FAILURE;
    }

    /**
     * ���ҵ������Ƿ�ΪNULL���Ա����ⲿ�����ж��Ƿ������������
     */
    public boolean isNullBusinessObject() {
        return null == this.businessObject;
    }
}
