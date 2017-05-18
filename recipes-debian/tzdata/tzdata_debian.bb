#
# Base recipe: meta/recipes-extended/tzdata/tzdata_2013i.bb
# Base branch: daisy
#

SUMMARY = "Timezone database"
HOMEPAGE = "ftp://elsie.nci.nih.gov/pub/"

PR = "r0"

inherit debian-package
PV = "2017b"

LICENSE = "PD & BSD & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ef1a352b901ee7b75a75df8171d6aca7"

DEPENDS = "zic-native"

DEFAULT_TIMEZONE ?= "Universal"

TIMEZONES := "africa antarctica asia australasia europe northamerica southamerica  \
		etcetera factory  backward systemv pacificnew"

# Follow debian/rules
do_compile () {
	# build the "default"; "posix" and "right" versions
	for zone in ${TIMEZONES}; do \
		${STAGING_SBINDIR_NATIVE}/zic -d ${WORKDIR}/build/${datadir}/zoneinfo -L /dev/null \
			-y ${S}/yearistype.sh ${S}/${zone} ; \
		${STAGING_SBINDIR_NATIVE}/zic -d ${WORKDIR}/build/${datadir}/zoneinfo/posix -L /dev/null \
			-y ${S}/yearistype.sh ${S}/${zone} ; \
		${STAGING_SBINDIR_NATIVE}/zic -d ${WORKDIR}/build/${datadir}/zoneinfo/right -L ${S}/leapseconds \
			-y ${S}/yearistype.sh ${S}/${zone} ; \
	done
}                                                      

# Follow debian/rules                                                                                
do_install () {                                                                 
	install -d ${D}${sbindir}
	install -m 0755 ${S}/debian/tzconfig ${D}${sbindir}
        install -d ${D}${datadir}/zoneinfo                    
        cp -pPR ${WORKDIR}/build/$exec_prefix ${D}/                                         
        # libc is removing zoneinfo files from package                          
        cp -pP "${S}/zone.tab" ${D}${datadir}/zoneinfo                          
        cp -pP "${S}/iso3166.tab" ${D}${datadir}/zoneinfo
	cp -pP "${S}/leap-seconds.list" ${D}${datadir}/zoneinfo

	# Install default timezone
	if [ -e ${D}${datadir}/zoneinfo/${DEFAULT_TIMEZONE} ]; then
		install -d ${D}${sysconfdir}
		echo ${DEFAULT_TIMEZONE} > ${D}${sysconfdir}/timezone
		ln -s ${datadir}/zoneinfo/${DEFAULT_TIMEZONE} ${D}${sysconfdir}/localtime
	else
		bberror "DEFAULT_TIMEZONE is set to an invalid value."
		exit 1
	fi	
        chown -R root:root ${D}                                                 
} 

pkg_postinst_${PN} () {                                                         
        etc_lt="$D${sysconfdir}/localtime"                                      
        src="$D${sysconfdir}/timezone"                                          
                                                                                
        if [ -e ${src} ] ; then                                                 
                tz=$(sed -e 's:#.*::' -e 's:[[:space:]]*::g' -e '/^$/d' "${src}")
        fi                                                                      
                                                                                
        if [ -z ${tz} ] ; then                                                  
                return 0                                                        
        fi                                                                      
                                                                                
        if [ ! -e "$D${datadir}/zoneinfo/${tz}" ] ; then                        
                echo "You have an invalid TIMEZONE setting in ${src}"           
                echo "Your ${etc_lt} has been reset to Universal; enjoy!"       
                tz="Universal"                                                  
                echo "Updating ${etc_lt} with $D${datadir}/zoneinfo/${tz}"      
                if [ -L ${etc_lt} ] ; then                                      
                        rm -f "${etc_lt}"                                       
                fi                                                              
                ln -s "${datadir}/zoneinfo/${tz}" "${etc_lt}"                   
        fi                                                                      
}                                                                               

# Packages primarily organized by directory with a major city                   
# in most time zones in the base package                                        
                                                                                
TZDATA_PACKAGES = "tzdata-posix tzdata-right tzdata-africa tzdata-atlantic\        
    tzdata-americas tzdata-antarctica tzdata-arctic tzdata-asia \               
    tzdata-australia tzdata-europe tzdata-pacific tzdata-misc"

PACKAGES = "${PN} ${TZDATA_PACKAGES}"

RDEPENDS_${PN} += "${TZDATA_PACKAGES}"

                                                                                
FILES_tzdata-africa += "${datadir}/zoneinfo/Africa/*"                           
RPROVIDES_tzdata-africa = "tzdata-africa"                                       
                                                                                
FILES_tzdata-americas += "${datadir}/zoneinfo/America/*  \                      
                ${datadir}/zoneinfo/US/*                \                       
                ${datadir}/zoneinfo/Brazil/*            \                       
                ${datadir}/zoneinfo/Canada/*            \                       
                ${datadir}/zoneinfo/Mexico/*            \                       
                ${datadir}/zoneinfo/Chile/*"                                    
RPROVIDES_tzdata-americas = "tzdata-americas" 

FILES_tzdata-antarctica += "${datadir}/zoneinfo/Antarctica/*"                   
RPROVIDES_tzdata-antarctica = "tzdata-antarctica"                               
                                                                                
FILES_tzdata-arctic += "${datadir}/zoneinfo/Arctic/*"                           
RPROVIDES_tzdata-arctic = "tzdata-arctic"

FILES_tzdata-asia += "${datadir}/zoneinfo/Asia/* \
		${datadir}/zoneinfo/Indian/*"
		
RPROVIDES_tzdata-asia = "tzdata-asia"

FILES_tzdata-atlantic += "${datadir}/zoneinfo/Atlantic/*"
RPROVIDES_tzdata-atlantic = "tzdata-atlantic"

FILES_tzdata-australia += "${datadir}/zoneinfo/Australia/*"
RPROVIDES_tzdata-australia = "tzdata-australia"

FILES_tzdata-europe += "${datadir}/zoneinfo/Europe/*"
RPROVIDES_tzdata-europe = "tzdata-europe"

FILES_tzdata-pacific += "${datadir}/zoneinfo/Pacific/*"
RPROVIDES_tzdata-pacific = "tzdata-pacific"

FILES_tzdata-posix += "${datadir}/zoneinfo/posix/*"
RPROVIDES_tzdata-posix = "tzdata-posix"

FILES_tzdata-right += "${datadir}/zoneinfo/right/*"
RPROVIDES_tzdata-right = "tzdata-right"

FILES_tzdata-misc += "${datadir}/zoneinfo/Cuba 		\
		${datadir}/zoneinfo/Egypt 		\
		${datadir}/zoneinfo/Eire 		\
		${datadir}/zoneinfo/Factory 		\
		${datadir}/zoneinfo/GB-Eire 		\
		${datadir}/zoneinfo/Hongkong 		\
		${datadir}/zoneinfo/Iceland		\
		${datadir}/zoneinfo/Iran 		\
		${datadir}/zoneinfo/Israel		\
		${datadir}/zoneinfo/Jamaica 		\
		${datadir}/zoneinfo/Japan 		\
		${datadir}/zoneinfo/Kwajalein 		\
		${datadir}/zoneinfo/Libya 		\
		${datadir}/zoneinfo/Navajo 		\
		${datadir}/zoneinfo/Poland 		\
		${datadir}/zoneinfo/Portugal 		\
		${datadir}/zoneinfo/Singapore 		\
		${datadir}/zoneinfo/Turkey"
RPROVIDES_tzdata-misc = "tzdata-misc"

FILES_${PN} += " ${sbindir}				 \
		${datadir}/zoneinfo/Pacific/Honolulu     \                      
                ${datadir}/zoneinfo/America/Anchorage    \                      
                ${datadir}/zoneinfo/America/Los_Angeles  \                      
                ${datadir}/zoneinfo/America/Denver       \                      
                ${datadir}/zoneinfo/America/Chicago      \                      
                ${datadir}/zoneinfo/America/New_York     \                      
                ${datadir}/zoneinfo/America/Caracas      \                      
                ${datadir}/zoneinfo/America/Sao_Paulo    \                      
                ${datadir}/zoneinfo/Europe/London        \                      
                ${datadir}/zoneinfo/Europe/Paris         \                      
                ${datadir}/zoneinfo/Africa/Cairo         \                      
                ${datadir}/zoneinfo/Europe/Moscow        \                      
                ${datadir}/zoneinfo/Asia/Dubai           \                      
                ${datadir}/zoneinfo/Asia/Karachi         \                      
                ${datadir}/zoneinfo/Asia/Dhaka           \                      
                ${datadir}/zoneinfo/Asia/Bankok          \                      
                ${datadir}/zoneinfo/Asia/Hong_Kong       \                      
                ${datadir}/zoneinfo/Asia/Tokyo           \                      
                ${datadir}/zoneinfo/Australia/Darwin     \                      
                ${datadir}/zoneinfo/Australia/Adelaide   \                      
                ${datadir}/zoneinfo/Australia/Brisbane   \                      
                ${datadir}/zoneinfo/Australia/Sydney     \                      
                ${datadir}/zoneinfo/Pacific/Noumea       \                      
                ${datadir}/zoneinfo/CET                  \                      
                ${datadir}/zoneinfo/CST6CDT              \                      
                ${datadir}/zoneinfo/EET                  \                      
                ${datadir}/zoneinfo/EST                  \                      
                ${datadir}/zoneinfo/EST5EDT              \                      
                ${datadir}/zoneinfo/GB                   \                      
                ${datadir}/zoneinfo/GMT                  \                      
                ${datadir}/zoneinfo/GMT+0                \
                ${datadir}/zoneinfo/GMT-0                \                      
                ${datadir}/zoneinfo/GMT0                 \                      
                ${datadir}/zoneinfo/Greenwich            \                      
                ${datadir}/zoneinfo/HST                  \                      
                ${datadir}/zoneinfo/MET                  \                      
                ${datadir}/zoneinfo/MST                  \                      
                ${datadir}/zoneinfo/MST7MDT              \                      
                ${datadir}/zoneinfo/NZ                   \                      
                ${datadir}/zoneinfo/NZ-CHAT              \                      
                ${datadir}/zoneinfo/PRC                  \                      
                ${datadir}/zoneinfo/PST8PDT              \                      
                ${datadir}/zoneinfo/ROC                  \                      
                ${datadir}/zoneinfo/ROK                  \                      
                ${datadir}/zoneinfo/UCT                  \                      
                ${datadir}/zoneinfo/UTC                  \                      
                ${datadir}/zoneinfo/Universal            \                      
                ${datadir}/zoneinfo/W-SU                 \                      
                ${datadir}/zoneinfo/WET                  \                      
                ${datadir}/zoneinfo/Zulu                 \                      
                ${datadir}/zoneinfo/zone.tab             \                      
                ${datadir}/zoneinfo/iso3166.tab          \                      
                ${datadir}/zoneinfo/Etc/*		 \
                ${datadir}/zoneinfo/SystemV		 \
                ${datadir}/zoneinfo/leap-seconds.list    \
		${sysconfdir}"
RDEPENDS_${PN} += "${TZDATA_PACKAGES}"		                                    

