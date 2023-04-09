package com.troiryan.modverify.common;

import java.util.Arrays;
import java.util.List;

public final class Constants {

	public static final String PROJECT_ID             = "{project_id}";
	public static final String PROJECT_NAME           = "{project_name}";
	public static final String DESCRIPTION            = "{description}";
	public static final String VERSION                = "{version}";
	public static final String AUTHOR                 = "{author}";

	public static final String[] REQUEST_CHANNEL      = { PROJECT_ID, "request_mods" };
	public static final byte PACKET_REQUEST_MODS_DISC = 42;
	public static final byte PACKET_MOD_LIST_DISC     = 43;

	public static final int MAXIMUM_MOD_ID_LEN        = 40;
	public static final int MAXIMUM_MOD_VERSION_LEN   = 20;

	public static final List<ModIdFilter> ignoredModsFilters = Arrays.asList(new ModIdFilter[] {
		new ModIdFilter("org_jetbrains_kotlin_*"),
		new ModIdFilter("fabric*"),
		new ModIdFilter("com_google*"),
		new ModIdFilter("java*"),
		new ModIdFilter("minecraft*"),
		new ModIdFilter("org_jetbrains*"),
		new ModIdFilter(PROJECT_ID + "*")
	});
}