results_path="scripts/output/"
n_values=(10 30 50 100)

for n in "${n_values[@]}"
do
  echo ${n} | java -classpath MapThatSet/bin/:scripts/ mapthatset.sim.MapThatSet > ${results_path}results_${n}.txt
  ruby scripts/results_analyzer.rb
  [ $? -eq 1 ] && exit 1
done

for n in "${n_values[@]}"
do
  mv ${results_path}results_${n}.txt ${results_path}old_results_${n}.txt
done
