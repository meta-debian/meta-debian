DEPENDS += "linux-ltsi-source"                                                       
                                                                                
deltask do_fetch                                                                
deltask do_unpack                                                               
deltask do_patch                                                                
                                                                                
do_configure[depends] += "linux-ltsi-source:do_patch"                                
do_populate_lic[depends] += "linux-ltsi-source:do_unpack"
