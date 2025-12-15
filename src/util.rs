use std::fs::File;
use std::io::Read;
use std::path::Path;

pub fn file_to_string(path: &str) -> String {
    let path = Path::new(path);
    let display = path.display();

    match File::open(&path) {
        Err(why) => panic!("couldn't open {}: {}", display, why),
        Ok(mut file) => {let mut s = String::new();
            match file.read_to_string(&mut s) {
                Err(why) => panic!("couldn't read {}: {}", display, why),
                Ok(_) => s,
            }
        },
    }
}