import * as React from 'react'
import IdleTimer from 'react-idle-timer'
import { IdleTimerProps } from 'react-idle-timer'
import * as Modal from 'react-modal'
import logout from 'corla/action/logout';

const customStyles = {
  content : {
    bottom                : 'auto',
    left                  : '50%',
    marginRight           : '50%',
    right                 : 'auto',
    top                   : '50%',
    transform             : 'translate(-50%, -50%)'
  }
};
 

export default class IdleDialogState extends React.Component {
  idleTimer  : IdleTimer | null
  idleTimerDialog : IdleTimer | null
  timeout : number
  timeoutDialog : number
  state : { 
    remaining: number
    isIdle: boolean
    isIdleDialog: boolean
    lastActive: Date
    elapsed: number
  }

  constructor(props: IdleTimerProps) {
    super(props)
   // ten minutes -- this.timeout = 600000  // 300000 - 5 minutes, 60000  - 1 minute 
   // 30 minutes 
   this.timeout = 3600000  // 300000 - 5 minutes, 60000  - 1 minute 
 
   // Testing one minute --  this.timeout = 60000  // 300000 - 5 minutes, 60000  - 1 minute 
    this.timeoutDialog =  120000 //60000 // 60000  - 1 minute 
    this.idleTimer = null
    this.idleTimerDialog = null
    this.state = {
      remaining: this.timeout,
      isIdle: false,
      isIdleDialog: false,
      lastActive: new Date(),
      elapsed: 0
    }
    // Bind event handlers and methods
    this.handleOnActive = this.handleOnActive.bind(this)
    this.handleOnClose = this.handleOnClose.bind(this)
    this.handleOnIdle = this.handleOnIdle.bind(this)
    this.handleReset = this.handleReset.bind(this)
    this.handlePause = this.handlePause.bind(this)
    this.handleResume = this.handleResume.bind(this)
    this.handleOnActiveDialog = this.handleOnActiveDialog.bind(this)
    this.handleOnIdleDialog = this.handleOnIdleDialog.bind(this)
  }

  componentDidMount() {
    this.setState({
      remaining: this.idleTimer && this.idleTimer.getRemainingTime(),
      lastActive: this.idleTimer && this.idleTimer.getLastActiveTime(),
      elapsed: this.idleTimer && this.idleTimer.getElapsedTime()
    })

    setInterval(() => {
      this.setState({
        remaining: this.idleTimer && this.idleTimer.getRemainingTime(),
        lastActive: this.idleTimer && this.idleTimer.getLastActiveTime(),
        elapsed: this.idleTimer && this.idleTimer.getElapsedTime()
      })
    }, 1000)
  }

  render() {
    return (
      <div>
        <IdleTimer
          ref={ref => { this.idleTimer = ref }}
          onActive={this.handleOnActive}
          onIdle={this.handleOnIdle}
          timeout={this.timeout}
        />
       <IdleTimer
          ref={ref => { this.idleTimerDialog = ref }}
          onActive={this.handleOnActiveDialog}
          onIdle={this.handleOnIdleDialog}
          timeout={this.timeoutDialog}
        />
        <Modal isOpen={this.state.isIdle}
        style={customStyles} >
        <h2>You've been idle for a while!</h2>
        <p>You will be logged out soon</p>
        <div>
          <button onClick={this.handleLogout}>Log me out</button>
          <button onClick={this.handleOnClose}>Keep me signed in</button>
        </div>
      </Modal>
      </div>
      
    )
  }

 
  handleOnClose() {
    this.setState({ isIdle: false })
    this.handleReset()
  }

  handleOnActive() {
    if(!this.idleTimer)
     this.setState({ isIdle: false })
  }

  handleOnIdle() {
	console.log('--------------handleOnIdle()--------------------------');
    this.setState({ isIdle: true })
  }

  handleLogout() {
    logout();
   }
 
  handleActive() {
    this.setState({ isIdle: false })
  }

  handleReset() {
    if (this.idleTimer) this.idleTimer.reset()
    if (this.idleTimerDialog) this.idleTimerDialog.reset()
  }

  handlePause() {
    if (this.idleTimer) this.idleTimer.pause()
  }

  handleResume() {
    if (this.idleTimer) this.idleTimer.resume()
  }

  // Logout if nothing selected
  handleOnIdleDialog() {
    this.handleLogout()
  }
  handleOnActiveDialog() {
    if(!this.idleTimerDialog)
     this.setState({ isIdleDialog: false })
  }


}
