package org.marketcetera.photon;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.Lifecycle;

public class SpringUtils {

	public static void startSpringBean(InitializingBean bean) throws Exception{
		bean.afterPropertiesSet();
	}
	
	public static void stopSpringBean(DisposableBean bean) throws Exception {
		bean.destroy();
	}

	public static void startSpringBean(Lifecycle lifecycleBean)
	{
		lifecycleBean.start();
	}

	public static void stopSpringBean(Lifecycle lifecycleBean){
		lifecycleBean.stop();
	}
	
}
