SUMMARY = "implementation of a "Singleton" class"
DESCRIPTION = "The Class::Singleton module implements a Singleton class from which other\n\
 classes can be derived.  A Singleton describes an object class that can have\n\
 only one instance in any system.  An example of a Singleton might be a print\n\
 spooler or system registry.\n\
 .\n\
 By itself, the Class::Singleton module does very little other than manage the\n\
 instantiation of a single object.  In deriving a class from Class::Singleton,\n\
 your module will inherit the Singleton instantiation method and can implement\n\
 whatever specific functionality is required.\n\
 .\n\
 For a description and discussion of the Singleton class, see\n\
 "Design Patterns", Gamma et al, Addison-Wesley, 1995, ISBN 0-201-63361-2."
HOMEPAGE = "http://search.cpan.org/dist/Class-Singleton/"

PR = "r0"
inherit debian-package
PV = "1.4"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=263;endline=267;md5=5633dc8e7615fa721afdee6fd5a663a3"
inherit cpan
# There is no debian/patches
DEBIAN_PATCH_TYPE = "nopatch"
