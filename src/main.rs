mod day_01;
mod util;

#[allow(dead_code)]
fn display_rotation(rotation: &day_01::Rotation) -> String {
    let dir = match rotation.direction {
        day_01::Direction::Left => "L",
        day_01::Direction::Right => "R",
    };
    format!(
"{{:direction {}
 :distance {}}}", dir, rotation.distance)
}

fn main() {
    use std::time::Instant;
    let now = Instant::now();

    // let result = day_01::parse_input();
    // println!("Result: {}", result.iter()
    //     .map(|rot| display_rotation(rot))
    //     .collect::<Vec<String>>().join("\n"));
    println!("Day 01 solution\npart 1: {}\npart 2: {}", day_01::solve_part1(), day_01::solve_part2());
    println!("Time: {:?}", now.elapsed());
}
