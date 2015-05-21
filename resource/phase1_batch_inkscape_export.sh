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

svg_file=$1
dest_dir=$2
num_amb_spot_drawables=$3
num_composite_drawables=$4
res_prefix=$5

# resolve commands
if command -v parallel >/dev/null 2>&1; then
    # echo "GNU parallel is available"
    iterator_cmd=parallel
    iterator_cmd_opt="-a -"
else
    # echo "GNU parallel is not available"
    iterator_cmd=xargs
    iterator_cmd_opt="-i"
fi

inkscape_export_obj=./inkscape_export_obj.sh


# create destination directory 
if [ ! -d $dest_dir ]; then
    mkdir $dest_dir
fi


# export
function inkscape_export_shadow_objs {
    local svg_file=$1
    local dest_dir=$2
    local name=$3
    local num=$4
    local scale=$5
    local out_prefix=$6
    local target_list=$(seq $num) # 1 .. num

    echo "$target_list" | \
        $iterator_cmd $iterator_cmd_opt \
        $inkscape_export_obj $svg_file $dest_dir/$out_prefix""$name"_z"{}"_x"$scale".9.png" $name"_z"{} $scale
}

inkscape_export_shadow_objs $svg_file $dest_dir "ambient_shadow" $num_amb_spot_drawables 1 $res_prefix
inkscape_export_shadow_objs $svg_file $dest_dir "ambient_shadow" $num_amb_spot_drawables 2 $res_prefix
inkscape_export_shadow_objs $svg_file $dest_dir "ambient_shadow" $num_amb_spot_drawables 3 $res_prefix
inkscape_export_shadow_objs $svg_file $dest_dir "ambient_shadow" $num_amb_spot_drawables 4 $res_prefix

inkscape_export_shadow_objs $svg_file $dest_dir "spot_shadow" $num_amb_spot_drawables 1 $res_prefix
inkscape_export_shadow_objs $svg_file $dest_dir "spot_shadow" $num_amb_spot_drawables 2 $res_prefix
inkscape_export_shadow_objs $svg_file $dest_dir "spot_shadow" $num_amb_spot_drawables 3 $res_prefix
inkscape_export_shadow_objs $svg_file $dest_dir "spot_shadow" $num_amb_spot_drawables 4 $res_prefix

inkscape_export_shadow_objs $svg_file $dest_dir "composite_shadow" $num_composite_drawables 1 $res_prefix
inkscape_export_shadow_objs $svg_file $dest_dir "composite_shadow" $num_composite_drawables 2 $res_prefix
inkscape_export_shadow_objs $svg_file $dest_dir "composite_shadow" $num_composite_drawables 3 $res_prefix
inkscape_export_shadow_objs $svg_file $dest_dir "composite_shadow" $num_composite_drawables 4 $res_prefix
