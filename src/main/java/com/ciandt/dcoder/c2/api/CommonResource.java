package com.ciandt.dcoder.c2.api;

import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.ciandt.dcoder.c2.entity.Card;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Path("/api")
@ThreadSafe
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Singleton
public class CommonResource {

	@Inject
	public CommonResource() {
		super();
	}

	@GET
	@Path("/hot-card")
	public Card hotCard(@Context HttpServletRequest request) {
		
		return null;
	}

}
