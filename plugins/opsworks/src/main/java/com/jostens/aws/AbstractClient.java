package com.jostens.aws;

import com.amazonaws.AmazonWebServiceClient;

/**
 * This is just a starting point for a Jostens AWS interface that can built
 * 
 * @author brovolc
 *
 * @param <T>
 */
public abstract class AbstractClient<T extends AmazonWebServiceClient> {

	protected abstract T getClient();
}
