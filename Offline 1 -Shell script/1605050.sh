#!/bin/bash
if [ $# -lt 1 ]; then
  echo "Please provide input file as command line argument next time.."
  exit
elif [ $# -eq 2 ]; then
  working_dir=$1
  #echo $working_dir

  FILE=$2
elif [[ $# -gt 2 ]]; then
  echo "You are giving too many arguments.."
else
  working_dir=$(pwd)
  FILE=$1
fi

dec=0
ynwa=0
until [ $dec == 1 ]; do
if test -f "$FILE"; then
    #echo "$FILE exist"
    dec=1
  else
    echo "Give input file name with extension:"
    read FILE
fi
done
i=0
while IFS= read -r line
 do
  var[$i]=$line
  i=`expr $i + 1`
done < "$FILE"
#working_dir="/home/ornob/Downloads/Offline 1 (shell script)/Shell_Script_Offline/working_dir"

out="/home/ornob/Downloads/output_dir"
[ ! -d "/home/ornob/Downloads/output_dir" ] && mkdir "/home/ornob/Downloads/output_dir"
[ ! -f "/home/ornob/Downloads/output_dir/out.csv" ] && touch "/home/ornob/Downloads/output_dir/out.csv"
cd "$out"
truncate -s 0 out.csv
echo "File Path,Line Number,Line containing search path" >> "out.csv"
cd "$working_dir"
#Main_processing_part
grep -ril "${var[2]}" | while read -r filename; do

link=$(readlink -f "$filename")
#echo $link
DIR=$(dirname "${link}")

file_name=$(basename "${filename}")
#echo $file_name
current=$(pwd)
#echo $current
cd "$DIR"

#echo $filename
line_count=$(wc -l "$file_name")
first=$(grep -winm1 "${var[2]}" "$file_name" | cut -d: -f1)

#last=$(grep -n 'Fedora' | tail -n1 | cut -d: -f1)

if [ "${var[0]}" == "begin" ]
then

  dif=`expr ${var[1]} - $first`
#echo "diff:""$dif""${var[1]}"

  if [ $dif -ge 0 ]
  then
    cp $file_name "/home/ornob/Downloads/output_dir"
    ynwa=$((ynwa+1))

    now=$(pwd)
    grep -nri "${var[2]}" "$file_name" | while read -r catch1
    do
        catch+="$link"","
        catch+="$(echo $catch1 | tr ":" ",")"
        cd "$out"
        echo $catch >> "out.csv"
        #echo $catch
        unset catch
        cd "$now"
    done

    cd "$out"
    DIR=${DIR//$working_dir/}
    filename=${DIR////.}
    filename="${filename:1}"
    filename+="."
    my_array=($(echo $file_name | tr "." "\n"))
    temp="${my_array[0]}""$first"
    #echo $temp
    filename+="$temp"".""${my_array[1]}"
    #filename+=
    #echo $filename
    #echo "\n"
    k=${filename::1}
    if [ $k == '.' ]
    then
    filename=${filename:1}
    fi

     mv -v "$file_name" "$filename"
  fi

elif [ "${var[0]}" == "end" ]; then
  #echo "Holla"
  #last=$(grep -n '${var[2]}' | tail -n1 | cut -d: -f1)
  last=$(grep -ni "${var[2]}" "$file_name" | cut -d: -f1 )
  last=$(echo $last | rev |cut -d" " -f1 |rev)
  #echo $last
  line_count=$(echo "${line_count//[!0-9]/}")
  dif=`expr $line_count - $last`

  if [ $dif -lt ${var[1]} ]; then
      #echo "ok"
    cp $file_name "/home/ornob/Downloads/output_dir"
    ynwa=$((ynwa+1))
    now=$(pwd)
    grep -nri "${var[2]}" "$file_name" | while read -r catch1
    do
        catch+="$link"","
        catch+="$(echo $catch1 | tr ":" ",")"
        cd "$out"
        echo $catch >> "out.csv"
        #echo $catch
        unset catch
        cd "$now"
    done
    #"/home/ornob/Downloads/output_dir/out.csv"
    cd "$out"
    #echo $catch >> "out.csv"
    #unset catch
    DIR=${DIR//$working_dir/}
    filename=${DIR////.}
    filename="${filename:1}"
    filename+="."
    my_array=($(echo $file_name | tr "." "\n"))
    temp="${my_array[0]}""$last"
    #echo $temp
    filename+="$temp"".""${my_array[1]}"
    #filename+=
    #echo $filename
    #echo "\n"
    k=${filename::1}
    if [ $k == '.' ]
    then
    filename=${filename:1}
  fi
    mv -v "$file_name" "$filename"
  fi
fi
cd "$current"
#pwd

echo $ynwa > "count.txt"
done

while IFS= read -r line
 do
  ynwa=$line
done < "count.txt"
rm "count.txt"
echo "Number of files matched criteria:"
echo $ynwa
