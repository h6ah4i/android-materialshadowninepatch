#!/bin/bash -e
#
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
#

src_dir=$1
dest_dir=$2
num_amb_spot_drawables=$3
num_composite_drawables=$4
res_prefix=$5

# create destination directory 
if [ ! -d $dest_dir ]; then
    mkdir $dest_dir
fi


# copy PNG files directory to drawable-nodpi
if [ -d $dest_dir/drawable-nodpi ]; then
    rm -r $dest_dir/drawable-nodpi
fi
cp -r $src_dir $dest_dir/drawable-nodpi

pushd $dest_dir

# ldpi x0.75   (NOTE: use mdpi resources)
mkdir drawable-ldpi || true
pushd drawable-ldpi
../../gen_alias_xml.py $res_prefix"ambient_shadow" $num_amb_spot_drawables 1
../../gen_alias_xml.py $res_prefix"spot_shadow" $num_amb_spot_drawables 1
../../gen_alias_xml.py $res_prefix"composite_shadow" $num_composite_drawables 1
popd

# mdpi x1.0
mkdir drawable-mdpi || true
pushd drawable-mdpi
../../gen_alias_xml.py $res_prefix"ambient_shadow" $num_amb_spot_drawables 1
../../gen_alias_xml.py $res_prefix"spot_shadow" $num_amb_spot_drawables 1
../../gen_alias_xml.py $res_prefix"composite_shadow" $num_composite_drawables 1
popd

# tvdpi x1.33   (NOTE: use mdpi resources)
mkdir drawable-tvdpi || true
pushd drawable-tvdpi
../../gen_alias_xml.py $res_prefix"ambient_shadow" $num_amb_spot_drawables 1
../../gen_alias_xml.py $res_prefix"spot_shadow" $num_amb_spot_drawables 1
../../gen_alias_xml.py $res_prefix"composite_shadow" $num_composite_drawables 1
popd

# hdpi  x1.5  (NOTE: use xhdpi resources)
mkdir drawable-hdpi || true
pushd drawable-hdpi
../../gen_alias_xml.py $res_prefix"ambient_shadow" $num_amb_spot_drawables 2
../../gen_alias_xml.py $res_prefix"spot_shadow" $num_amb_spot_drawables 2
../../gen_alias_xml.py $res_prefix"composite_shadow" $num_composite_drawables 2
popd

# xhdpi x2.0
mkdir drawable-xhdpi || true
pushd drawable-xhdpi
../../gen_alias_xml.py $res_prefix"ambient_shadow" $num_amb_spot_drawables 2
../../gen_alias_xml.py $res_prefix"spot_shadow" $num_amb_spot_drawables 2
../../gen_alias_xml.py $res_prefix"composite_shadow" $num_composite_drawables 2
popd

# xxhdpi x3.0
mkdir drawable-xxhdpi || true
pushd drawable-xxhdpi
../../gen_alias_xml.py $res_prefix"ambient_shadow" $num_amb_spot_drawables 3
../../gen_alias_xml.py $res_prefix"spot_shadow" $num_amb_spot_drawables 3
../../gen_alias_xml.py $res_prefix"composite_shadow" $num_composite_drawables 3
popd

# xxxhdpi x4.0
mkdir drawable-xxxhdpi || true
pushd drawable-xxxhdpi
../../gen_alias_xml.py $res_prefix"ambient_shadow" $num_amb_spot_drawables 4
../../gen_alias_xml.py $res_prefix"spot_shadow" $num_amb_spot_drawables 4
../../gen_alias_xml.py $res_prefix"composite_shadow" $num_composite_drawables 4
popd

popd