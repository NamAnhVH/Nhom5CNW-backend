package com.WebLearning.WebLearning.Controllers;

import com.WebLearning.WebLearning.FormData.CourseFormDto;
import com.WebLearning.WebLearning.FormData.LectureFormDto;
import com.WebLearning.WebLearning.FormData.TeacherProfileDto;
import com.WebLearning.WebLearning.Service.AccountService;
import com.WebLearning.WebLearning.Service.CourseService;
import com.WebLearning.WebLearning.Service.LectureService;
import com.WebLearning.WebLearning.Service.TeacherProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping(path = "/teacher")
public class TeacherController {

    @Autowired
    private TeacherProfileService teacherProfileService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private LectureService lectureService;

    @GetMapping
    public String teacherPage() {
        return "teacher/teacherPage";
    }

    @GetMapping("/profile")
    public String teacherProfilePage(Model model) {
        model.addAttribute("user", teacherProfileService.getCurrentAccountProfile());
        return "teacher/profileManager/profilePage";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute TeacherProfileDto profile){
        try {
            teacherProfileService.updateProfile(profile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/teacher/profile";
    }

    @GetMapping("/course/listCourse")
    public String listCoursePage(Model model){
        model.addAttribute("listCourse", courseService.getAllByCurrentAccount());
        return "teacher/courseManager/listCoursePage";
    }

    @GetMapping("/course/listCourse/unapprovedCourse")
    public String listUnapprovedCoursePage(Model model){
        model.addAttribute("listCourse", courseService.getAllUnapprovedCourseByCurrentTeacher());
        return "teacher/courseManager/listCoursePage";
    }

    @GetMapping("/course/addCourse")
    public String addCoursePage(Model model){
        model.addAttribute("newCourse", new CourseFormDto());
        return "teacher/courseManager/addCoursePage";
    }

    @PostMapping("/course/addCourse")
    public String addCourseAction(@ModelAttribute CourseFormDto newCourse) throws IOException {
        courseService.addCourse(newCourse);
        return "redirect:/teacher/course/listCourse";
    }

    @GetMapping("/course/{id}/previewCourse")
    public String previewCoursePage(@PathVariable Long id, Model model){
        if(!courseService.isAccessAllowed(id)){
            return "redirect:/teacher/course/listCourse";
        }
        model.addAttribute("course", courseService.getCourseById(id));
        model.addAttribute("listLecture", lectureService.getLectureByCourseId(id));
        return "teacher/courseManager/previewCoursePage";
    }

    @GetMapping("/course/{id}/editCourse")
    public String editCoursePage(@PathVariable Long id, Model model){
        if(!courseService.isAccessAllowed(id)){
            return "redirect:/teacher/course/listCourse";
        }
        model.addAttribute("course", courseService.getCourseById(id));
        return "teacher/courseManager/editCoursePage";
    }

    @PostMapping("/course/{id}/editCourse")
    public String editCourseAction(@PathVariable Long id, @ModelAttribute CourseFormDto courseDto) throws IOException {
        courseService.updateCourse(id, courseDto);
        return "redirect:/teacher/course/" + id + "/previewCourse";
    }

    @PostMapping("/course/{id}/deleteCourse")
    public String deleteCourseAction(@PathVariable Long id){
        courseService.deleteCourse(id);
        return "redirect:/teacher/course/listCourse";
    }

    @PostMapping("account/deleteAccount")
    public String deleteAccountAction(){
        accountService.deleteTeacherAccount();
        return "redirect:/logout";
    }

    @GetMapping("/course/{id}/addLecture")
    public String addLecturePage(@PathVariable Long id, Model model){
        if(!courseService.isAccessAllowed(id)){
            return "redirect:/teacher/course/listCourse";
        }
        model.addAttribute("newLecture", new LectureFormDto());
        model.addAttribute("id", id);
        return "teacher/lectureManager/addLecturePage";
    }

    @PostMapping("/course/{id}/addLecture")
    public String addLectureAction(@PathVariable Long id, @ModelAttribute LectureFormDto lectureFormDto){
        lectureService.addLecture(id, lectureFormDto);
        return "redirect:/teacher/course/" + id + "/previewCourse";
    }

    @GetMapping("/course/{id}/manageLecture")
    public String manageLecturePage(@PathVariable Long id, Model model){
        if(!courseService.isAccessAllowed(id)){
            return "redirect:/teacher/course/listCourse";
        }
        model.addAttribute("id", id);
        model.addAttribute("listLecture", lectureService.getLectureByCourseId(id));
        return "teacher/lectureManager/listLecturePage";
    }

    @GetMapping("/course/{courseId}/manageLecture/{lectureId}")
    public String editLecturePage(@PathVariable("courseId") Long courseId, @PathVariable("lectureId") Long lectureId, Model model){
        if(!courseService.isAccessAllowed(courseId)){
            return "redirect:/teacher/course/listCourse";
        }
        model.addAttribute("id", courseId);
        model.addAttribute("lecture", lectureService.getLectureById(lectureId));
        return "teacher/lectureManager/editLecturePage";
    }

    @PostMapping("/course/{courseId}/manageLecture/{lectureId}/editLecture")
    public String editLectureAction(@PathVariable("courseId") Long courseId, @PathVariable("lectureId") Long lectureId, @ModelAttribute("lecture") LectureFormDto lectureFormDto){
        lectureService.updateLecture(lectureId, lectureFormDto);
        return "redirect:/teacher/course/" + courseId + "/manageLecture";
    }

    @PostMapping("/course/{courseId}/manageLecture/{lectureId}/deleteLecture")
    public String deleteLectureAction(@PathVariable("courseId") Long courseId, @PathVariable("lectureId") Long lectureId){
        lectureService.deleteLecture(lectureId);
        return "redirect:/teacher/course/" + courseId + "/manageLecture";
    }

}
