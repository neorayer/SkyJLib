package com.skymiracle.logger;

public class SimpleAppLogger implements Loggable {

	private String loggerName;

	public SimpleAppLogger() {
		this.loggerName = getClass().getSimpleName();
	}

	private String chgMsg(String s) {
		return this.loggerName + ": " + s;
	}

	private StringBuffer chgMsg(StringBuffer sb) {
		return new StringBuffer().append(this.loggerName).append(": ").append(
				sb);
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#warn(java.lang.String, java.lang.Exception)
	 */
	public void warn(String s, Exception e) {
		Logger.warn(chgMsg(s), e);

	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#warn(java.lang.String)
	 */
	public void warn(String s) {
		Logger.warn(chgMsg(s));

	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#error(java.lang.String, java.lang.Exception)
	 */
	public void error(String s, Exception e) {
		Logger.error(chgMsg(s), e);
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#error(java.lang.String)
	 */
	public void error(String s) {
		Logger.error(chgMsg(s));
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#info(java.lang.StringBuffer)
	 */
	public void info(StringBuffer sb) {
		Logger.info(chgMsg(sb));
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#info(java.lang.String)
	 */
	public void info(String s) {
		Logger.info(chgMsg(s));
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#fatalError(java.lang.StringBuffer)
	 */
	public void fatalError(StringBuffer sb) {
		Logger.info(chgMsg(sb));
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#fatalError(java.lang.StringBuffer, java.lang.Exception)
	 */
	public void fatalError(StringBuffer sb, Exception e) {
		Logger.info(chgMsg(sb), e);
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#fatalError(java.lang.String, java.lang.Exception)
	 */
	public void fatalError(String s, Exception e) {
		Logger.info(chgMsg(s), e);
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#info(java.lang.StringBuffer, java.lang.Exception)
	 */
	public void info(StringBuffer sb, Exception e) {
		Logger.info(chgMsg(sb), e);
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#debug(java.lang.String)
	 */
	public void debug(String s) {
		Logger.debug(chgMsg(s));
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#detail(java.lang.String)
	 */
	public void detail(String s) {
		Logger.detail(chgMsg(s));
	}

	/* (non-Javadoc)
	 * @see com.skymiracle.logger.Logger1#debug(java.lang.StringBuffer)
	 */
	public void debug(StringBuffer sb) {
		Logger.debug(chgMsg(sb));

	}

}
