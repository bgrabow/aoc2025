mod day_01;
mod util;

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
    let result = day_01::parse_input();
    println!("Result: {}", result.iter()
        .map(|rot| display_rotation(rot))
        .collect::<Vec<String>>().join("\n"));
}
