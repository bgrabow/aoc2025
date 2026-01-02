pub mod day_01;
pub mod util;

pub fn solve() {
    use std::time::Instant;
    let now = Instant::now();

    // let result = day_01::parse_input();
    // println!("Result: {}", result.iter()
    //     .map(|rot| display_rotation(rot))
    //     .collect::<Vec<String>>().join("\n"));
    let input = util::file_to_string("resources/input_01.txt");
    println!("Day 01 solution\npart 1: {}\npart 2: {}",
             day_01::solve_part1(&input),
             day_01::solve_part2(&input));
    println!("Time: {:?}", now.elapsed());
}

