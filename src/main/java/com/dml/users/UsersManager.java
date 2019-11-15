package com.dml.users;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户和授权管理
 * 如果进行反序列化，会报如下错误。所以，尽量优化一下数据结构，不要使用Object作为map的key
 * @author neo
 *
 */
public class UsersManager {

	private Map<String, User> userIdUserMap = new HashMap<>();

	private Map<String, Authorization> authKeyAuthMap = new HashMap<>();

	private Map<String, String> authKeyUserIdMap = new HashMap<>();

	/**
	 * 授权，如果成功的话返回授权的用户id。
	 * 
	 * @param authKey
	 *            识别某个授权的key
	 * @param authParameters
	 *            授权计算所需要的参数
	 * @return 授权的用户id
	 * @throws AuthorizationNotFoundException
	 *             通过key找不到相关授权
	 * @throws AuthException
	 *             授权失败
	 */
	public String authAndGetUserId(AuthKey authKey, String... authParameters)
			throws AuthorizationNotFoundException, AuthException {
		Authorization auth = authKeyAuthMap.get(authKey);
		if (auth != null) {
			auth.auth(authParameters);
			return authKeyUserIdMap.get(authKey);
		} else {
			throw new AuthorizationNotFoundException();
		}
	}

	/**
	 * 创建新用户及其授权
	 * 
	 * @param userId
	 * @param auth
	 */
	public void createUserWithAuth(String userId, Authorization auth) throws AuthorizationAlreadyExistsException {
		AuthKey authKey = auth.createAuthKey();
		if (!authKeyAuthMap.containsKey(authKey)) {
			User user = new User();
			user.setId(userId);
			userIdUserMap.put(userId, user);
			authKeyAuthMap.put(authKey.getPublisher()+"-"+authKey.getUuid(), auth);
			authKeyUserIdMap.put(authKey.getPublisher()+"-"+authKey.getUuid(), userId);
		} else {
			throw new AuthorizationAlreadyExistsException();
		}
	}

	/**
	 * 为某个用户添加一个授权
	 * 
	 * @param userId
	 * @param auth
	 * @throws UserNotFoundException
	 * @throws AuthorizationAlreadyExistsException
	 */
	public void addAuthForUser(String userId, ThirdAuthorization auth)
			throws UserNotFoundException, AuthorizationAlreadyExistsException {
		AuthKey authKey = auth.createAuthKey();
		if (!authKeyAuthMap.containsKey(authKey)) {
			if (userIdUserMap.containsKey(userId)) {
				authKeyAuthMap.put(authKey.getPublisher()+"-"+authKey.getUuid(), auth);
				authKeyUserIdMap.put(authKey.getPublisher()+"-"+authKey.getUuid(), userId);
			} else {
				throw new UserNotFoundException();
			}
		} else {
			throw new AuthorizationAlreadyExistsException();
		}
	}

	public Map<String, User> getUserIdUserMap() {
		return userIdUserMap;
	}

	public void setUserIdUserMap(Map<String, User> userIdUserMap) {
		this.userIdUserMap = userIdUserMap;
	}


	public Map<String, Authorization> getAuthKeyAuthMap() {
		return authKeyAuthMap;
	}

	public void setAuthKeyAuthMap(Map<String, Authorization> authKeyAuthMap) {
		this.authKeyAuthMap = authKeyAuthMap;
	}

	public Map<String, String> getAuthKeyUserIdMap() {
		return authKeyUserIdMap;
	}

	public void setAuthKeyUserIdMap(Map<String, String> authKeyUserIdMap) {
		this.authKeyUserIdMap = authKeyUserIdMap;
	}
}
