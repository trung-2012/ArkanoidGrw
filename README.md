Arkanoid Game – Object-Oriented Programming Project  
---

Tác giả  
---
**Nhóm 1** – Lớp **TH OOP INT2204 11 - I2526**  
+ 👨‍💻 Lê Ngọc Phong – 24021593  
+ 👨‍💻 Phạm Văn Trung – 24021649  
+ 👨‍💻 Nguyễn Văn Tùng – 24021665  
+ 👨‍💻 Nguyễn Gia Vĩ – 24021673

👨‍🏫 **Giảng viên hướng dẫn**: Kiều Văn Tuyên  
📅 **Học kì**: HK1 - năm học 2025-2026  

📘 Mô tả dự án
---

Đây là trò chơi Arkanoid được phát triển bằng Java 17 + JavaFX trong khuôn khổ môn Lập trình Hướng Đối Tượng.
Dự án thể hiện khả năng áp dụng các nguyên lý OOP, tổ chức cấu trúc theo mô-đun, sử dụng JavaFX để xây dựng giao diện và xây dựng một game hoàn chỉnh từ đầu.

✨ **Tính năng chính**
+ 🧩Trò chơi được phát triển bằng Java 17+ cùng JavaFX cho giao diện đồ hoạ (GUI), sử dụng FXML + CSS để tách biệt logic và hiển thị.  
+ 🧱Áp dụng đầy đủ các nguyên lý OOP: Đóng gói (Encapsulation), Kế thừa (Inheritance), Đa hình (Polymorphism) và Trừu tượng hóa (Abstraction).  
+ 📦Tổ chức code theo mô-đun rõ ràng gồm Controller – Manager – Model – View giúp dễ mở rộng và bảo trì.  
+ 🏭Áp dụng một số mẫu thiết kế (Design Patterns) như Singleton (GameSettings, SoundManager), Strategy/Factory cho các loại gạch & power-up, giúp game linh hoạt hơn.  
+ 🚀Sử dụng đa luồng gián tiếp thông qua JavaFX Application Thread để đảm bảo gameplay mượt mà và giao diện phản hồi nhanh.  
+ 💥Hệ thống hiệu ứng hình ảnh & âm thanh đầy đủ, bao gồm hiệu ứng nổ gạch, hiệu ứng debris, blur khi pause, fade animation, và âm thanh va chạm/nhạc nền.
+ 🔊Sử dụng kéo thả để điều chỉnh âm thanh.
+ 🛡Hệ thống Power-up phong phú, bao gồm Shield, LaserBeam, MultiBall, Paddle_Grow, Paddle_Shrink, Weak, Strong.  
+ 🎨Mỗi skin paddle đều có hiệu ứng Power_up laserBeam riêng.  
+ 👤Hệ thống người chơi (Player Management) với đăng nhập, chọn nickname, lưu điểm và quản lý dữ liệu qua file.  
+ 🏆Tích hợp bảng xếp hạng (Leaderboard) cho phép người chơi xem và so sánh điểm số.  
+ 🗺Màn hình Settings hoàn chỉnh hỗ trợ đổi skin bóng/paddle, xem trước (preview) và hoàn toàn có thể sử dụng khi chuyển sang pause_game.
+ 🌀Hỗ trợ nhiều màn chơi (levels) thông qua hệ thống LevelLoader đọc file .txt và tự cập nhật hình nền theo từng màn.  
+ 👾Ngoài các loại gạch cơ bản Game còn hỗ trợ loại gạch đặc biệt SecretBrick với khả năng biến hình sau mỗi 10 giây. SecretBrick không chỉ thay đổi ngoại hình mà còn chuyển đổi hoàn toàn hành vi sang loại gạch mới (Normal, Wood, Iron, Gold, Explode, Insane). Cơ chế này tạo ra tính bất ngờ trong gameplay vì sức bền, điểm số và tác động của viên gạch có thể thay đổi liên tục theo thời gian.

**Các hiệu ứng**  
+ 🧩Hiệu ứng mảnh vụn rơi khi gạch bị phá hủy. Các mảnh vụn sẽ rơi xuống dưới với trọng lực và độ trong suốt giảm dần.
+ 💣Gạch nổ khi bị phá hủy sẽ trigger explosion effect.
+ 🔥InsaneBrick - Gạch siêu cứng : Có cơ chế đặc biệt: khi va chạm với ball hoặc laser, có 5% khả năng bị vỡ ngay lập tức bất kể health còn lại.
+ 🎬Render intro animation khi bắt đầu level. Hiển thị "LEVEL X" với hiệu ứng RGB split glitch và scan line.
+ 🌈Render level clear animation khi hoàn thành level. Hiển thị hiệu ứng burst và fade to white.
+ ⏳Render countdown animation (3, 2, 1, START!) trước khi bắt đầu chơi. Hiển thị số đếm ngược với hiệu ứng RGB split, ring expansion và particles.
+ 🌠Bóng có trail effect (hiệu ứng vệt đuôi) khi di chuyển.
+ 🌀Hiệu ứng loading cho từng level.  

🕹**Cơ chế hoạt động trong game**
+ Điều khiển paddle để đánh bóng
+ Đập gạch để ghi điểm
+ Nhận vật phẩm rơi xuống để buff
+ Qua màn khi phá hết các viên gạch
+ Mỗi cấp độ có map nền khác nhau
+ Thua khi rơi hết số mạng

🧩UML Diagram
---
![UML Diagram](src/main/resources/game/arkanoid/readme_images/UML.png)  
Diagram được generate bằng IntelliJ IDEA.  
**🎯 Chức năng**: mô tả toàn bộ cấu trúc class, quan hệ kế thừa, association và dependency giữa các module Controller, Manager, Model, View.  

Design Patterns được sử dụng
---
🔒**1. Singleton Pattern**
Sử dụng trong:  
+ GameSettings  
+ SoundManager  
+ PowerUpManager

**Mục đích:**
Đảm bảo mỗi thành phần quan trọng của game chỉ tồn tại một instance duy nhất, giúp quản lý tài nguyên tập trung, giảm xung đột trạng thái và tránh tạo đối tượng không cần thiết.

🏭**2. Factory Method / Simple Factory Pattern**
Ứng dụng trong:  
+ Tạo các loại gạch (NormalBrick, WoodBrick, IronBrick, GoldBrick, ExplodeBrick, InsaneBrick)  
+ SecretBrick tạo disguise brick dựa trên BrickType  

**Mục đích:**
+ Tách rời logic khởi tạo object khỏi gameplay.  
+ Việc thêm một loại gạch mới trở nên dễ dàng mà không phải chỉnh sửa nhiều vị trí trong code.  

🎭**3. Strategy Pattern**
Ứng dụng trong:  
+ Các loại Brick có hành vi khác nhau khi nhận damage  
+ Power-up có cách xử lý riêng (Laser, Shield, MultiBall…)  
+ SecretBrick thay đổi “behavior” khi biến hình sang loại gạch khác  

**Mục đích:**
Cho phép thay đổi hành vi của từng đối tượng tại runtime mà không cần sửa code gốc, giúp game mở rộng linh hoạt và giảm if-else phức tạp.  

🔄**4. State Pattern**
Ứng dụng trong:  
+ SecretBrick: liên tục chuyển đổi trạng thái sang các loại Brick khác  
+ Hệ thống Pause/Resume trong MainController  
+ GameEngine chuyển đổi giữa trạng thái running, paused, resetting  

**Mục đích:**
Tách biệt các trạng thái của đối tượng và hành vi tương ứng, giúp quản lý logic theo trạng thái rõ ràng và tránh các điều kiện lồng nhau.  

👁**5. Observer / Callback Pattern**
Ứng dụng trong:  
+ PowerUpManager gọi callback khi kích hoạt power-up  
+ Controllers cập nhật giao diện theo dữ liệu từ GameEngine  
+ Pause/Settings sử dụng callback để thay đổi skin ingame  
+ LevelLoader/Managers thông báo thay đổi lên MainController  

**Mục đích:**
Tăng khả năng giao tiếp giữa các module mà không làm chúng phụ thuộc chặt vào nhau (loose coupling).  

🤝**6. Delegation Pattern**
Ứng dụng đặc biệt trong:  
+ SecretBrick: chuyển việc xử lý damage, update, render cho “disguise brick”
+ Một số manager gọi lại controller để cập nhật UI

**Mục đích:**
Chia trách nhiệm đúng nơi, giúp SecretBrick mang hành vi chính xác của gạch mà nó biến hình thành mà không phải viết lại toàn bộ logic.  

🧰Installation
---
+ 📥Clone the project from the repository.
+ 🛠Open the project in the IDE.
+ ▶️Run the project.

🎯Hướng dẫn chơi
---
### 🎮Phím điều khiển 
| ⌨️Phím | 🕹Hành động |
|------------|-----------|
| ← | Di chuyển paddle sang trái |
| →	| Di chuyển paddle sang phải |
| SPACE |	Thả bóng |
### 🖱Các hành động khi click chuột
---

| 🖼Biểu tượng | ⚡Hành động |📄Mô tả |
|------------|-----------|--------|
| ![](src/main/resources/game/arkanoid/images/start.png) | Start Game | Bắt đầu trò chơi, chuyển sang màn MainView |
| ![](src/main/resources/game/arkanoid/images/settings.png) | Settings | Mở màn hình cài đặt, đổi skin và tùy chỉnh game |
| ![](src/main/resources/game/arkanoid/images/leaderboardDemo.png) | leaderboardDemo | Mở bảng xếp hạng |
| ![](src/main/resources/game/arkanoid/images/logout.png) | log out | Đăng xuất |
| ![](src/main/resources/game/arkanoid/images/pause.png) | Pause | Tạm dừng trò chơi và mở Pause Menu |
| ![](src/main/resources/game/arkanoid/images/prev.png) | Preview | Xem trước các màn |
| ![](src/main/resources/game/arkanoid/images/PlayAgain.png) | PlayAgain | Chơi lại |
| ![](src/main/resources/game/arkanoid/images/backToMain.png) | Back to Menu | Trở về menu chính khi đang chơi |
| ![](src/main/resources/game/arkanoid/images/resetCurrentLv.png) | PlayAgain | Chơi lại khi đang chơi dở |
| ![](src/main/resources/game/arkanoid/images/continue.png) | Continue | Tiếp tục chơi |


### 🎁Các Power-up trong game
| 🖼Biểu tượng | 🔌Tên | ✨Tác dụng |
|-----------|-----|-----------|
| ![](src/main/resources/game/arkanoid/images/multi_ball.png) | MultiBall | Sinh ra thêm 2 quả bóng tối đa 5 quả |
| ![](src/main/resources/game/arkanoid/images/laser.png) | LaserBeam | Bắn tia laser phá gạch 5 lần|
| ![](src/main/resources/game/arkanoid/images/shield.png) | Shield | Chặn 3 lần |
| ![](src/main/resources/game/arkanoid/images/extra_life.png) | Extra_life | Tăng số mạng lên 1 , giới hạn 5 |
| ![](src/main/resources/game/arkanoid/images/paddle_grow.png) | Paddle_Grow | Tăng chiều dài và chiều rộng thanh paddle |
| ![](src/main/resources/game/arkanoid/images/paddle_shrink.png) | Paddle_Shrink | Giảm chiều dài và chiều rộng thanh paddle |
| ![](src/main/resources/game/arkanoid/images/Weak.png) | Weak | Giảm tốc độ bóng |
| ![](src/main/resources/game/arkanoid/images/Strong.png) | Strong | Tăng tốc độ bóng |

**🧮Hệ thống tính điểm**
| Loại gạch	| ❤️Máu (Health) | ⭐Điểm (Points) |
| ---------	| ------------ | ------------- |
| Normal | 1 |	10 |
| Wood | 2 | 20 |
| Iron | 3 | 40 |	
| Gold | 4 | 50 |
| Explode | 2	|	30 |	
| Insane | 100 | 1000	|
| Secret | 1000 | 1	|

## 📸Demo giao diện
---

### 🔐Login
![Login](src/main/resources/game/arkanoid/readme_images/login_demo.png)

### 📝Register  
![Register](src/main/resources/game/arkanoid/readme_images/dangky_demo.png)  

### 🏁Start Menu
![Start Menu](src/main/resources/game/arkanoid/readme_images/StartMenu_demo.png)

### ⚙️Option

![Option](src/main/resources/game/arkanoid/readme_images/option_demo.png)

### 👀Preview

![Preview](src/main/resources/game/arkanoid/readme_images/prev_demo.png)

### 🎮Gameplay
<img src="src/main/resources/game/arkanoid/readme_images/gameplay.png" width="500">

### ⏸Pause

![Pause](src/main/resources/game/arkanoid/readme_images/pause_demo.png)

### 🏆Leaderboard
![Leaderboard](src/main/resources/game/arkanoid/readme_images/leaderboard_demo.png)

### 🏁EndGame

![EndGame](src/main/resources/game/arkanoid/readme_images/end_demo.png)

## 🎥 Gameplay Demo
[![Watch the video](https://img.youtube.com/vi/drTlYjZBf0U/0.jpg)](https://www.youtube.com/watch?v=drTlYjZBf0U)  
🎮 *Trải nghiệm gameplay Arkanoid – JavaFX – OOP – UET – VNU với đầy đủ hiệu ứng, power-up và animation!*  

🚀Hướng phát triển trong tương lai
---
**Các tính năng đã lên kế hoạch**  

**🕹Thêm chế độ chơi**  
+ ⏱Time Attack
+ ♾Endless Survival
+ 👥Multiplayer

**🎮Cải thiện gameplay**  
+ 👹Boss level
+ 🪄Thêm loại Power-up mới
+ 🏅Hệ thống Achievement

**🛠Nâng cấp kỹ thuật**  
+ 🔗Chuyển sang LibGDX / JavaFX nâng cao
+ ✨Particle effects + animation đẹp hơn
+ 🌐Kết nối leaderboard online  

### 🛠Công nghệ sử dụng
---

| 🔧Công nghệ | 🔢Phiên bản | 🎯Mục đích |
|-----------|-----------|-----------|
| **Java** | 17+ | Ngôn ngữ lập trình chính của dự án |
| **JavaFX** | 17–21 | Xây dựng giao diện người dùng (UI) và đồ họa |
| **Maven** | 3.9+ | Quản lý dependency và build project |
| **CSS** | — | Tạo style cho giao diện JavaFX |

📜License
---
+ Dự án phục vụ mục đích học tập và nghiên cứu.
+ Vui lòng tuân thủ quy định về trung thực học thuật.

📝Ghi chú
---
+ Đây là dự án môn Lập trình Hướng Đối Tượng
+ Toàn bộ code được phát triển bởi các thành viên trong nhóm
+ Một số hình ảnh/âm thanh được sử dụng cho mục đích học thuật

---

*Cập nhật lần cuối: [12/11/2025]*
