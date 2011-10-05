def get_scores(filename)
  return [100000, 100000, 1000000] unless File.exists?(filename)
  results = []
  File.open(filename) do |file|
    last_line = file.read.split("\n").last
    last_line.scan(/\d+\t/).each do |match|
      results << Integer(match)
    end
  end
  results
end

root_path = 'scripts/output/'
threshold = 0.9

Dir.foreach(root_path) do |path|
  next if path.include?('old_') or path == '.' or path == '..'
  old_scores = get_scores("#{root_path}old_#{path}")
  new_scores = get_scores(root_path + path)

  old_sum = old_scores.inject(:+)
  new_sum = new_scores.inject(:+)

  if old_sum * 1.0 / new_sum < threshold
    puts "Possible regression detected"
    puts "Old data:"
    p old_scores
    p "New data:"
    p new_scores
    exit 1
  end

end

