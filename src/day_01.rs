use crate::util;

pub enum Direction {
    Left,
    Right,
}

pub struct Rotation {
    pub direction: Direction,
    pub distance: i32,
}

fn rotations(input: &str) -> Vec<Rotation> {
    input
        .trim()
        .lines()
        .map(|s| {
            let (dir_char, dist_str) = s.split_at(1);
            let direction = match dir_char {
                "L" => Direction::Left,
                "R" => Direction::Right,
                _ => panic!("Invalid direction character"),
            };
            let distance: i32 = dist_str.parse().expect("Invalid distance number");
            Rotation { direction, distance }
        }).collect()
}

pub fn parse_input() -> Vec<Rotation> {
    rotations(&util::file_to_string("resources/input_01.txt"))
}
