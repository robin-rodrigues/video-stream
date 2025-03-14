import { useState } from "react";
import "./App.css";
import VideoUpload from "./components/VideoUpload";
import { Toaster } from "react-hot-toast";

function App() {
    const [count, setCount] = useState(0);

    const [videoId, setVideoId] = useState(
        "42695871-bacc-45e3-b9fa-d420c62b59ac"
    );

    return (
        <>
            <Toaster />
            <div className="flex flex-col items-center space-y-9 justify-center py-9">
                <h1 className="text-2xl font-bold text-gray-700 dark:text-gray-100">
                    Video Streaming App
                </h1>

                <div className="flex mt-14 w-full justify-around">
                    <div>
                        <h1 className="text-white">Playing Video</h1>
                        <video
                            style={{
                                width: 500,
                                height: 300,
                            }}
                            src={`http://localhost:8080/api/v1/videos/stream/range/${videoId}`}
                            controls
                        ></video>
                    </div>

                    <VideoUpload />
                </div>
            </div>
        </>
    );
}

export default App;
