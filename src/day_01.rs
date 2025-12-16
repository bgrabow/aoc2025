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

fn cycle(value: i32, min: i32, max: i32) -> i32 {
    let range_size = max - min + 1;
    let mut adjusted_value = value - min;
    adjusted_value = adjusted_value % range_size;
    if adjusted_value < 0 {
        adjusted_value += range_size;
    }
    adjusted_value + min
}

pub fn solve_part1() -> String {
    let dial_start = 50;
    let dial_max = 99;
    let dial_min = 0;

    let password_num = parse_input().iter()
        .map(|rot| {
        match rot.direction {
            Direction::Left => {
                -rot.distance
            },
            Direction::Right => {
                rot.distance
            },
        }
    }).fold(vec![dial_start], |acc, x| {
        let last = *acc.last().unwrap();
        let mut new_acc = acc;
        new_acc.push(cycle(last + x, dial_min, dial_max));
        new_acc
    }).iter().filter(|&x| x == &0).count();

    password_num.to_string()
}
