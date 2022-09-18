import React, { useEffect, useState } from "react";
import { Button, message,Modal } from "antd";
import {
  getUsers,
  getChatContacts,
  countNewMessages,
  findChatMessages,
  findChatMessage,
} from "../util/ApiUtil";
import { useRecoilValue, useRecoilState } from "recoil";
import {
  loggedInUser,
  chatActiveContact,
  chatMessages,
} from "../atom/globalState";
import ScrollToBottom from "react-scroll-to-bottom";
import "./Chat.css";

var stompClient = null;
const Chat = (props) => {
  const currentUser = useRecoilValue(loggedInUser);
  const [text, setText] = useState("");
  const [contacts, setContacts] = useState([]);
  const [chatContacts, setChatContacts] = useState([]);
  const [activeContact, setActiveContact] = useRecoilState(chatActiveContact);
  const [messages, setMessages] = useRecoilState(chatMessages);

  useEffect(() => {
    if (localStorage.getItem("accessToken") === null) {
      props.history.push("/login");
    }
    connect();
    loadContacts();
    loadChatContacts();
  }, []);

  useEffect(() => {
    if (activeContact === undefined) return;
    findChatMessages(activeContact.id, currentUser.id).then((msgs) =>{
      console.log(msgs,"mesagessssssss")
      setMessages(msgs)
    }

    );
    loadContacts();
    loadChatContacts();
  }, [activeContact]);

  const connect = () => {
    const Stomp = require("stompjs");
    var SockJS = require("sockjs-client");
    SockJS = new SockJS("http://localhost:8080/ws");
    stompClient = Stomp.over(SockJS);
    stompClient.connect({}, onConnected, onError);
  };

  const onConnected = () => {
    console.log("connected");
    console.log(currentUser);
    stompClient.subscribe(
      "/user/" + currentUser.id + "/queue/messages",
      onMessageReceived
    );
  };

  const onError = (err) => {
    console.log(err);
  };

  const onMessageReceived = (msg) => {
    const notification = JSON.parse(msg.body);
    const active = JSON.parse(sessionStorage.getItem("recoil-persist"))
      .chatActiveContact;

    if (active.id === notification.senderId) {
      findChatMessage(notification.id).then((message) => {
        const newMessages = JSON.parse(sessionStorage.getItem("recoil-persist"))
          .chatMessages;
        newMessages.push(message);
        setMessages(newMessages);
      });
    } else {
      message.info("Received a new message from " + notification.senderName);
    }
    loadContacts();
    loadChatContacts();
  };

  const sendMessage = (msg) => {
 
      const message = {
        senderId: currentUser.id,
        recipientId: activeContact.id,
        senderName: currentUser.name,
        recipientName: activeContact.name,
        content: msg,
        timestamp: new Date(),
      };
      stompClient.send("/app/chat", {}, JSON.stringify(message));

      const newMessages = [...messages];
      newMessages.push(message);
      setMessages(newMessages);
      loadContacts();
      loadChatContacts();
   
  };
  const [isModalVisible, setIsModalVisible] = useState(false);

  const showModal = () => {
    setIsModalVisible(true);
  };

  const handleOk = () => {
    setIsModalVisible(false);
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };
  const loadContacts = () => {
    console.log("loadContacts")
    const promise = getUsers(currentUser.id).then((users) =>
      users.map((contact) =>
      countNewMessages(contact.id, currentUser.id).then((count) => {
        contact.newMessages = count;
        return contact;
      })
    ) 
    );

    promise.then((promises) =>
      Promise.all(promises).then((users) => {
        setContacts(users);
        if (activeContact === undefined && users.length > 0) {
          setActiveContact(users[0]);
        }
      })
    );
  };

  const loadChatContacts = () => {
    console.log("loadChatContacts")
    const promise = getChatContacts(currentUser.id).then((users) =>
      users.map((contact) =>
      countNewMessages(contact.id, currentUser.id).then((count) => {
        contact.newMessages = count;
        return contact;
      })
    ) 
    );

    promise.then((promises) =>
      Promise.all(promises).then((users) => {
        setChatContacts(users);
        if (activeContact === undefined && users.length > 0) {
          setActiveContact(users[0]);
        }
      })
    );
  };
  return (
    <div id="frame">
      <div id="sidepanel">
        <div id="profile">
          <div className="wrap">
            <img
              id="profile-img"
              src={currentUser.profilePicture}
              className="online"
              alt=""
            />
            <p>{currentUser.name}</p>
            <div id="status-options">
              <ul>
                <li id="status-online" className="active">
                  <span className="status-circle"></span> <p>Online</p>
                </li>
                <li id="status-away">
                  <span className="status-circle"></span> <p>Away</p>
                </li>
                <li id="status-busy">
                  <span className="status-circle"></span> <p>Busy</p>
                </li>
                <li id="status-offline">
                  <span className="status-circle"></span> <p>Offline</p>
                </li>
              </ul>
            </div>
          </div>
        </div>
        <div id="search" />
        
        <div id="contacts">
          <ul>
            {chatContacts.map((chatContacts) => (
              <li
                onClick={() => setActiveContact(chatContacts)}
                className={
                  activeContact && chatContacts.id === activeContact.id
                    ? "contact active"
                    : "contact"
                }
              >
                <div className="wrap">
                  <span className="contact-status online"></span>
                  <img id={chatContacts.id} src={chatContacts.profilePicture} alt="" />
                  <div className="meta">
                    <p className="name">{chatContacts.name}</p>
                    {chatContacts.newMessages !== undefined &&
                      chatContacts.newMessages > 0 && (
                        <p className="preview">
                          {chatContacts.newMessages} new messages
                        </p>
                      )}
                  </div>
                </div>
              </li>
            ))}
          </ul>
        </div>
        <div id="bottom-bar">
          <button id="addcontact" onClick={showModal}>
            <i className="fa fa-user fa-fw" aria-hidden="true"></i>{" "}
            <span>AddContact</span>
          </button>
          <Modal title="Contact List" visible={isModalVisible} onOk={handleOk} onCancel={handleCancel}>
          <div id="contacts2">
          <ul>
            {contacts.map((contact) => (
              <li
                onClick={() => {
                  setActiveContact(contact)
                  setIsModalVisible(false)
                }}
                className={
                  activeContact && contact.id === activeContact.id
                    ? "contact active"
                    : "contact"
                }
              >
                <div className="wrap">
                  <span className="contact-status online"></span>
                  {/* <img id={contact.id} src={contact.profilePicture} alt="" /> */}
                  <div className="meta">
                    <p className="name">{contact.name}</p>
                      <hr></hr>
                  </div>
                </div>
              </li>
            ))}
          </ul>
          </div>
      </Modal>
          <button id="settings">
            <i className="fa fa-cog fa-fw" aria-hidden="true"></i>{" "}
            <span>Settings</span>
          </button>
        </div>
      </div>
      <div className="content">
        <div className="contact-profile">
          <img src={activeContact && activeContact.profilePicture} alt="" />
          <p>{activeContact && activeContact.name}</p>
        </div>
        <ScrollToBottom className="messages">
          <ul>
            {messages.map((msg) => (
              <li className={msg.senderId === currentUser.id ? "sent" : "replies"}>
                {msg.senderId !== currentUser.id && (
                  <img src={activeContact.profilePicture} alt="" />
                )}
                <p>{msg.content}</p>
              </li>
            ))}
          </ul>
        </ScrollToBottom>
        <div className="message-input">
          <div className="wrap">
            <input
              name="user_input"
              size="large"
              placeholder="Write your message..."
              value={text}
              onChange={(event) => setText(event.target.value)}
              onKeyPress={(event) => {
                if (event.key === "Enter") {
                  sendMessage(text);
                  setText("");
                }
              }}
            />

            <Button
              icon={<i className="fa fa-paper-plane" aria-hidden="true"></i>}
              onClick={() => {
                sendMessage(text);
                setText("");
              }}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Chat;
