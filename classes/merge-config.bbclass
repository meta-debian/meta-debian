#
# merge-config.bbclass
#
# merge_config() function is based on merge_config.sh script in
# yocto-kernel-tools that is released under the following license.
#
# Copyright (c) 2009-2010 Wind River Systems, Inc.
# Copyright 2011 Linaro
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
#

#
# This function creates the same .config as "merge_config.sh -m",
# but is more simple. merge_config.sh is included in yocto-kernel-tools.
#
# usage: merge_config <config1> <config2> ... <configN>
# output: .config
#
# If the same CONFIG is defined in the both of configN and configN+1,
# the CONFIG value in configN+1 is selected (the value in configN is dropped).
#
merge_config() {
	SED_CONFIG_EXP="s/^\(# \)\{0,1\}\(CONFIG_[a-zA-Z0-9_]*\)[= ].*/\2/p"

	[ -n "${1}" ] || return
	cp ${1} .config.merged
	while [ -n "${2}" ]; do
		for cfg in $(sed -n "${SED_CONFIG_EXP}" ${2}); do
			if grep -q -w ${cfg} .config.merged; then
				sed -i "/${cfg}[ =]/d" .config.merged
			fi
		done
		cat ${2} >> .config.merged
		shift
	done
	mv .config.merged .config
}

# This function comes from the busybox recipe.
# Returns all the elements from the SRC_URI that are .config files.
# Usurally, this function is used with merge_config as follows:
#   merge_config defconfig ${@" ".join(find_cfgs(d))}
def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        if s.endswith('.config'):
            sources_list.append(s)

    return sources_list
