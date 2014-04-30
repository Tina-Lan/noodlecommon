
package org.fengling.noodlecommon.dbrwseparate.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fengling.noodlecommon.dbrwseparate.datasource.DataSourceContextHolder;
import org.fengling.noodlecommon.dbrwseparate.datasource.DataSourceType;
import org.fengling.noodlecommon.dbrwseparate.msloadbalancer.MSDataSourceModel;
import org.fengling.noodlecommon.dbrwseparate.msloadbalancer.MSDataSourcesLoadBalancerManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class TuanServiceTemplateImpl implements TuanServiceTemplate {

    private final static Log logger = LogFactory.getLog(TuanServiceTemplateImpl.class);

    protected TransactionTemplate transactionTemplate;

    public TuanCallbackResult execute(final TuanServiceCallback action, final Object domain) {

        if (logger.isDebugEnabled()) {
            logger.debug("����ģ�巽����ʼ����");
        }
        
        TuanCallbackResult result = TuanCallbackResult.success();

        try {
        	 // ��������Դ����д����
        	DataSourceContextHolder.setDataSourceType(DataSourceType.MASTER);
        	result = action.executeCheck();
            if (result.isSuccess()) {
                result = (TuanCallbackResult) this.transactionTemplate.execute(new TransactionCallback<TuanCallbackResult>() {
                        public TuanCallbackResult doInTransaction(TransactionStatus status) {

                            // 3. �ص�ҵ���߼�
                            // 3.1 ͨ��annotation��ʵ��ĳЩoption���͵���չ
                            TuanCallbackResult iNresult = action.executeAction();
                            if (null == iNresult) {
                                throw new TuanServiceException(TuanServiceConstants.SERVICE_NO_RESULT);
                            }

                            // 4. ��չ��
                            templateExtensionInTransaction(iNresult);
                            if (iNresult.isFailure()) {
                                status.setRollbackOnly();
                                return iNresult;
                            }
                            return iNresult;
                        }
                        
                    });
               
                if (result.isSuccess()) {
                    templateExtensionAfterTransaction(result);
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("�����˳�ģ�巽��");
            }
        } catch (TuanServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��A��", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);
        } catch (RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��B��", e);
            }
            result = TuanCallbackResult.failure(0, e);
        } catch (Throwable e) {
           
            if (logger.isErrorEnabled()) {
                logger.error("�쳣�˳�ģ�巽��C��", e);
            }
            result = TuanCallbackResult.failure(TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);
        }finally{
            DataSourceContextHolder.clearDataSourceType();
        }
        
        return result;
    }
    
    public TuanCallbackResult executeWithoutTransaction(final TuanServiceCallback action, final Object domain) {

        if (logger.isDebugEnabled()) {
            logger.debug("����ģ�巽����ʼ����");
        }
        TuanCallbackResult result = TuanCallbackResult.success();
        // ��������Դ����д����
        
        MSDataSourceModel mSDataSourceModel = MSDataSourcesLoadBalancerManager.getAliveMSDataSource();
        
        if(mSDataSourceModel==null){
        	return TuanCallbackResult.failure(TuanServiceConstants.NO_ALIVE_DATASOURCE);
        }

        try {
        	DataSourceContextHolder.setDataSourceType(mSDataSourceModel.getDataSourceType());
            result = action.executeCheck();
            if (result.isSuccess()) {
                result = action.executeAction();
                if (null == result) {
                    throw new TuanServiceException(TuanServiceConstants.SERVICE_NO_RESULT);
                }

                // 4. ��չ��
                templateExtensionAfterExecute(result);
                if (result.isFailure()) {
                    return result;
                }
                // 5. ����ҵ���¼�
                
            }
        } catch (TuanServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��D��", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);

        } catch (Throwable e) {
            // ��ϵͳ�쳣ת��Ϊ�����쳣
            if (logger.isErrorEnabled()) {
                logger.error("�쳣�˳�ģ�巽��F��", e);
            }
            result = TuanCallbackResult.failure(TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);
        }finally{
        	 DataSourceContextHolder.clearDataSourceType();
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("ģ��ִ�н���");
        }
       
        return result;
    }
    
    public TuanCallbackResult executeWithoutTransactionCheckOrder(final TuanServiceCallback action, final Object domain) {

        if (logger.isDebugEnabled()) {
            logger.debug("����ģ�巽����ʼ����");
        }
        
        TuanCallbackResult result = TuanCallbackResult.success();
        // ��������Դ����д����
        
        MSDataSourceModel mSDataSourceModel = MSDataSourcesLoadBalancerManager.getAliveMSDataSource();
        
        if(mSDataSourceModel==null){
        	return TuanCallbackResult.failure(TuanServiceConstants.NO_ALIVE_DATASOURCE);
        }
        
        try {
        	//DataSourceContextHolder.setDataSourceType(mSDataSourceModel.getDataSourceType());
        	DataSourceContextHolder.setDataSourceType(DataSourceType.MASTER);
            result = action.executeCheck();
        
            if (result.isSuccess()) {
                result = action.executeAction();
                if (null == result) {
                    throw new TuanServiceException(TuanServiceConstants.SERVICE_NO_RESULT);
                }

                // 4. ��չ��
                templateExtensionAfterExecute(result);
                if (result.isFailure()) {
                    return result;
                }
                // 5. ����ҵ���¼�
                
            }
        } catch (TuanServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��D��", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);
        } catch (Throwable e) {
            // ��ϵͳ�쳣ת��Ϊ�����쳣
            if (logger.isErrorEnabled()) {
                logger.error("�쳣�˳�ģ�巽��F��", e);
            }
            result = TuanCallbackResult.failure(TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);
        } finally{
        	 DataSourceContextHolder.clearDataSourceType();
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("ģ��ִ�н���");
        }
       
        return result;
    }
    
    
    /**
     * �ֱ�����ר��--ǰ�ᣬ�����в����ı���ͬһ����
     */
    public TuanCallbackResult executeSubmeter(final TuanServiceCallback action, final Object domain , final TransactionTemplate transactionTemplate) {

        if (logger.isDebugEnabled()) {
            logger.debug("����ģ�巽����ʼ����");
        }
        TuanCallbackResult result = TuanCallbackResult.success();
       
        try {
             // ��������Դ����д����
            DataSourceContextHolder.setDataSourceType(DataSourceType.MASTER);
            result = action.executeCheck();
            if (result.isSuccess()) {
                result = (TuanCallbackResult) transactionTemplate.execute(new TransactionCallback<TuanCallbackResult>() {
                        public TuanCallbackResult doInTransaction(TransactionStatus status) {

                            // 3. �ص�ҵ���߼�
                            // 3.1 ͨ��annotation��ʵ��ĳЩoption���͵���չ
                            TuanCallbackResult iNresult = action.executeAction();
                            if (null == iNresult) {
                                throw new TuanServiceException(TuanServiceConstants.SERVICE_NO_RESULT);
                            }

                            // 4. ��չ��
                            templateExtensionInTransaction(iNresult);
                            if (iNresult.isFailure()) {
                                status.setRollbackOnly();
                                return iNresult;
                            }
                            return iNresult;
                        }
                    });
               
                if (result.isSuccess()) {
                    templateExtensionAfterTransaction(result);
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("�����˳�ģ�巽��");
            }
        } catch (TuanServiceException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��A��", e);
            }
            result = TuanCallbackResult.failure(e.getErrorCode(), e);
        } catch (RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("�쳣�˳�ģ�巽��B��", e);
            }
            result = TuanCallbackResult.failure(0, e);
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("�쳣�˳�ģ�巽��C��", e);
            }
            result = TuanCallbackResult.failure(TuanServiceConstants.SERVICE_SYSTEM_FALIURE, e);
        }finally{
            DataSourceContextHolder.clearDataSourceType();
        }
        
        return result;
    }
    
    /**
     * ��չ�㣺ģ���ṩ������ͬ����ҵ����<b>������</b>������չ��һ����
     */
    protected void templateExtensionInTransaction(TuanCallbackResult result) {
    }

   
    protected void templateExtensionAfterTransaction(TuanCallbackResult result) {
    }

   
    protected void templateExtensionAfterExecute(TuanCallbackResult result) {
    }
    
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
