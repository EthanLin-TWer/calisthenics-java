package com.theladders.avital.cc;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationTest {
	@Test
	public void itsLockedDown() throws Exception {
		assertThat(new Application(1).calculate(1), is(2));
	}
}
