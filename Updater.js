const fs = require('fs');
const { resolve } = require('path');
const axios = require('axios');

fs.watch(
  resolve(__dirname, 'org/firstinspires/ftc/teamcode'),
  {
    persistent: true,
    recursive: true,
  },
  (type, file) => {
    switch (type) {
      case 'change': {
        console.log(`Change detected in "${file}"`);

        axios
          .post(
            `http://192.168.43.1:8080/java/file/save?f=/src/org/firstinspires/ftc/teamcode/${file}`,
            {
              data: fs.readFileSync(resolve(__dirname, 'org/firstinspires/ftc/teamcode', file), 'utf-8'),
            },
            {
              headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
              },
            }
          )
          .then(res => {
            if (res.status == 200) {
              console.log('Saved!');
            } else {
              console.error(res);
              process.exit(1);
            }
          });

        break;
      }

      case 'rename': {
        console.warn(`File "${file}" was renamed, this is not handled!`);

        break;
      }
    }
  }
);

console.log('Updater Ready!\n\n');
