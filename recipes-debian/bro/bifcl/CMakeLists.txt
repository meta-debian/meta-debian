# Intended to only build "bifcl" (BIF compiler) binary from bro source package.

cmake_minimum_required(VERSION 2.8 FATAL_ERROR)

project(BifCl C CXX)

include(cmake/CommonCMakeConfig.cmake)

#
# Dependencies
#

include(FindRequiredPackage)

FindRequiredPackage(BISON)
FindRequiredPackage(FLEX)

#
# Parser & scanner
#

# Required to include header files in the source tree
include_directories(BEFORE ${CMAKE_CURRENT_SOURCE_DIR})

set(BISON_FLAGS "--debug")

# BIF parser/scanner
bison_target(BIFParser builtin-func.y
             ${CMAKE_CURRENT_BINARY_DIR}/bif_parse.cc
             HEADER ${CMAKE_CURRENT_BINARY_DIR}/bif_parse.h
             #VERBOSE ${CMAKE_CURRENT_BINARY_DIR}/bif_parse.output
             COMPILE_FLAGS "${BISON_FLAGS}")
flex_target(BIFScanner builtin-func.l ${CMAKE_CURRENT_BINARY_DIR}/bif_lex.cc)
add_flex_bison_dependency(BIFScanner BIFParser)

#
# bifcl
#

set(bifcl_SRCS
   ${BISON_BIFParser_INPUT}
   ${FLEX_BIFScanner_INPUT}
   ${BISON_BIFParser_OUTPUTS}
   ${FLEX_BIFScanner_OUTPUTS}
   bif_arg.cc
   module_util.cc
   bif_arg.h
   module_util.h
)

add_executable(bifcl ${bifcl_SRCS})

install(TARGETS bifcl DESTINATION bin)
