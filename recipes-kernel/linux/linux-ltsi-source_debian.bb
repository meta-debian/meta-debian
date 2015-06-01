require linux-ltsi-common.inc

deltask do_configure
deltask do_compile
deltask do_install
deltask do_populate_sysoot
deltask do_package
deltask do_package_write_deb
deltask do_package_write_rpm                                                    
deltask do_package_write_ipk                                                    
deltask do_package_qa                                                           
deltask do_packagedata                                                          
deltask do_rm_work

SRC_URI += "file://${MACHINE}.defconfig"

PROVIDES += "nativesdk-linux-ltsi-source"
