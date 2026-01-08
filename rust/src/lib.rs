pub mod day_01;
mod day_02;
pub mod util;

pub fn solve() {
    use std::time::Instant;
    let now = Instant::now();
    
    let input = util::file_to_string("resources/input_01.txt");
    println!("Day 01 solution");
    println!("part 1: {}", day_01::solve_part1(&input));
    println!("part 2: {}", day_01::solve_part2(&input));
    let input = util::file_to_string("resources/input_02.txt");
    println!("Day 02 solution");
    println!("part 1: {}", day_02::solve_part1(&input));
    println!("part 2: {}", day_02::solve_part2(&input));
    println!("Time: {:?}", now.elapsed());
}

