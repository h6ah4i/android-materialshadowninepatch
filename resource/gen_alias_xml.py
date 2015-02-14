#!/usr/bin/python

#    Copyright (C) 2015 Haruki Hasegawa
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.


import sys

# check arguments
if len(sys.argv) < 4:
	print 'Usage: ' + sys.argv[0] + 'res_name num scale'
	exit()

def generate_xml(res_name, scale, zdepth):
	xml_fname = res_name + '_z' + str(zdepth) + '.xml'
	src_res_name = res_name + '_z' + str(zdepth) + '_x' + scale
	f = open(xml_fname, 'w')

	f.write('<?xml version="1.0" encoding="utf-8"?>\n')
	f.write('<nine-patch xmlns:android="http://schemas.android.com/apk/res/android"\n')
	f.write('    xmlns:tools="http://schemas.android.com/tools"\n')
	f.write('    android:src="@drawable/' + src_res_name +'"\n')
	f.write('    tools:ignore="unused" />\n')

	f.close()


# main process
res_name = sys.argv[1]
num = int(sys.argv[2])
scale = sys.argv[3]
for x in range(1, num + 1):
	generate_xml(res_name, scale, x)