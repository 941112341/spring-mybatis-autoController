package framework.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WrapResponse {

	@Pointcut(value = "execution(* com.dao.*.*(..))")
	public void point(){}

	/*@Around(value = "execution(* com.cn.hkvision.dao.*.*(..))")
	public Object changeResult(ProceedingJoinPoint proceedingJoinPoint) {
		Object[] args = proceedingJoinPoint.getArgs();
		try {
			Object result = proceedingJoinPoint.proceed(args);
			return BaseInfo.success(result);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}*/


	/*@AfterReturning
	public void afterReturnWrap() {

	}*/

}
