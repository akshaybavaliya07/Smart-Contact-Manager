// const baseURL = "http://localhost:8080"
const baseURL = "smartcontactmanagerapp.ap-south-1.elasticbeanstalk.com"

// options with default values
const options = {
    placement: 'bottom-right',
    backdrop: 'dynamic',
    backdropClasses:
        'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
    closable: true,
    onHide: () => {},
    onShow: () => {},
    onToggle: () => {},
};

// ----- Contact View Modal ---------
const contactViewModal = document.querySelector('#contact_view_modal');

// instance options object
const instanceOptions = {
  id: 'contact_view_modal',
  override: true
};

const contactModal = new Modal(contactViewModal, options, instanceOptions);

function openContactModal() {
    contactModal.show();
}

function closeContactModal() {
    contactModal.hide();
}

async function loadContactData(id) {
    try {
        const data = await fetch(`${baseURL}/api/contacts/${id}`);
        const contact = await data.json();
        
        document.querySelector('#contact-image').src = contact.picture;
        document.querySelector('#contact-name').textContent = contact.name;
        document.querySelector('#contact-email').textContent = contact.email;
        document.querySelector('#contact-phone').textContent = contact.phoneNumber;
        document.querySelector('#contact-address').textContent = contact.address;
        document.querySelector('#contact-about').textContent = contact.description;
        document.querySelector('#contact-address').textContent = contact.address;
        document.querySelector("#contact-facebook").href = contact.facebookLink;
        document.querySelector("#contact-facebook").textContent = contact.facebookLink;
        document.querySelector("#contact-linkedIn").href = contact.linkedinLink;
        document.querySelector("#contact-linkedIn").textContent = contact.linkedinLink;

        openContactModal();
    } catch (error) {
        console.error('Error:', error);
    }
}


// ----- Contact Delete Alert Modal ---------
const contactDeleteAlertModal = document.querySelector('#delete_contact_alert');

// instance options object
const deleteInstanceOptions = {
    id: 'delete_contact_alert',
    override: true
  };

const deleteAlertModal = new Modal(contactDeleteAlertModal, options, deleteInstanceOptions);

let selectedContactId = null;
function openDeleteAlertModal(id) {
    selectedContactId = id;
    deleteAlertModal.show();
}

function closeDeleteAlertModal() {
    selectedContactId = null;
    deleteAlertModal.hide();
}

async function deleteContact() {
    try {
        const response = await fetch(`${baseURL}/user/contacts/delete/${selectedContactId}`);
        if (response.ok) {
            location.reload();
        }
    } catch (error) {
        console.error('Error:', error);
    }
}


// ---------- Resend Email verification link modal ----------------

const resendEmailVerificationModal = document.querySelector('#resend_verificationLink_view_modal');

const resendEmailVerificationInstanceOptions = {
    id:'resend_verificationLink_view_modal',
    override: true
}

let emailVerificationModal;

if(resendEmailVerificationModal) {
    emailVerificationModal = new Modal(resendEmailVerificationModal, options, resendEmailVerificationInstanceOptions);
}

function openResendEmailVerificationModal() {
    emailVerificationModal.show();
}

function closeResendEmailVerificationModal() {
    emailVerificationModal.hide();
}


//  --------- Export contacts to XML ---------------
function exportData() {
    TableToExcel.convert(document.getElementById('contact-table'), {
        name: "Contacts.xlsx",
        sheet: "Sheet1",
    })
}